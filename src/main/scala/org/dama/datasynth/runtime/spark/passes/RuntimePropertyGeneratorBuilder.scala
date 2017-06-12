package org.dama.datasynth.runtime.spark.passes

import java.io.{File, PrintWriter}

import org.dama.datasynth.DataSynthConfig
import org.dama.datasynth.executionplan.ExecutionPlan.{PropertyGenerator, PropertyTable, StaticValue, Value}
import org.dama.datasynth.executionplan.{ExecutionPlan, ExecutionPlanNonVoidVisitor, ExecutionPlanVoidVisitor}
import org.dama.datasynth.runtime.spark.operators.{EvalValueOperator, FetchRndGeneratorOperator}

import scala.tools.nsc.io.{JManifest, Jar, JarWriter}
import scala.tools.nsc.{GenericRunnerSettings, Global, Settings}
import scala.reflect.runtime.universe.typeOf

/**
  * Created by aprat on 23/05/17.
  * This Visitor processes an execution plan and for each property generator, it generates its PropertyGenerator
  * Interface coutnerpart, compiles it and packages all of them into a jar
  */
class RuntimePropertyGeneratorBuilder(config : DataSynthConfig) extends ExecutionPlanNonVoidVisitor[Map[String,(String,String)]] {

  /**
    * Processes an execution plan and generates a jar with property generators generated at runtime,
    * that implement the PropertyGenerator interface
    * @param jarFileName The name of the jar file to create
    * @param executionPlan The execution plan to process
    * @return A the map between origina property generators classnames and
    *         the new ones
    */
  def buildJar( jarFileName : String, executionPlan : Seq[ExecutionPlan.Table]) : Map[String,String] = {

    /** Writing .scala files **/
    val classes = executionPlan.foldLeft(Map[String,(String,String)]())( (currentDeclarations, nextNode) => currentDeclarations ++ nextNode.accept(this))
    val sourceFileNames : List[String] = classes.toList.map( {case (tableName,(className,classDecl)) => {
      val fileName = s"${config.driverWorkspaceDir}/$className.scala"
      val writer = new PrintWriter(new java.io.File(fileName))
      writer.write(classDecl)
      writer.close()
      fileName
    }})
    classes.foreach({case (_,(_,decl)) => println(decl)})

    /** Compiling .scala files **/
    val settings = new GenericRunnerSettings(println _)
    settings.nc.value = true
    settings.usejavacp.value = true
    settings.outputDirs.setSingleOutput(s"${config.driverWorkspaceDir}")
    val currentJarPath : String = getClass().getProtectionDomain().getCodeSource().getLocation().getPath()
    println(currentJarPath)
    settings.classpath.append(currentJarPath)
    val g = new Global(settings)
    val run = new g.Run
    run.compile(sourceFileNames)

    /** Building JAR **/
    val jar : JarWriter = new JarWriter(new scala.reflect.io.File(new java.io.File(jarFileName)), new JManifest())
    sourceFileNames.map( file => file.replace(".scala",".class")).foreach( file => jar.addFile(new scala.reflect.io.File( new java.io.File(file)),""))
    jar.close()

    classes.map( { case (propertyTable,(className,_)) => (propertyTable -> className) })
  }

  /**
    * Generates the name of a generated proeprty generator given an original generator name
    * @param name The name of the original property generator
    * @return The new name for the generated property generator
    */
  private def mkGeneratedClassName( name : String ): String = {
    name.replace(".","").toUpperCase()
  }

  /**
    * Creates a "match" expression given a property table and an index
    * @param propertyTable The property table execution node
    * @param index The index in the dependencies list of the match to generate
    * @return The string with the match expression
    */
  private def mkMatch( propertyTable : PropertyTable[_], index : Integer ): String = {
    val returnTypeName = propertyTable.tag.tpe.toString()
    s"dependencies($index) match { case value : $returnTypeName => value }"
  }

  /**
    * Generates a class declaration given a property generator execution plan node
    * @param info The execution plan node representing the property generated to generate the new generator from
    * @tparam T The data type returned by the given property generator
    * @return  The class declaration
    */
  private[passes] def generatePGClassDefinition[T](className : String, info : PropertyGenerator[T]) : String = {
    val returnTypeName = info.tag.tpe.toString
    val initParameters : String = if(info.initParameters.length == 0) {
      " "
    } else {
      info.initParameters.map({
        case parameter: StaticValue[String@unchecked] if parameter.tag.tpe =:= typeOf[String] => s"""\"${parameter.value.toString}\""""
        case parameter: StaticValue[Float@unchecked] if parameter.tag.tpe =:= typeOf[Float] => s"${parameter.value.toString}f"
        case parameter: StaticValue[_] => s"${parameter.value.toString}"
        case _ => throw new RuntimeException("Unable to evaluate initialiation parameter. Is this a StaticValue?")
      })
      .reduceLeft((current,next) => current+","+next )
    }
    val propertyGeneratorName = info.className
    val sufix = mkGeneratedClassName(className)
    val dependentGenerators = info.dependentPropertyTables.map( table => mkGeneratedClassName(table.name))
    val dependentGeneratorsCallList : String = info.dependentPropertyTables.zipWithIndex.foldLeft("")( {case (current,(table,index)) => s"$current,${mkMatch(table,index)}"})

    s"import org.dama.datasynth.runtime.spark.utils._\n" +
    s"import org.dama.datasynth.common.generators.property._\n" +
    s"class $sufix extends PropertyGenerator[$returnTypeName] {\n" +
    s"val generator$sufix = new $propertyGeneratorName($initParameters)\n"+
    s" def run(id : Long, random : Long, dependencies : Any*) : $returnTypeName = generator$sufix.run(id,random$dependentGeneratorsCallList)\n" +
    s"}\n"
  }

  override def visit(node: ExecutionPlan.StaticValue[_]): Map[String, (String,String)] = {
    Map.empty
  }

  override def visit(node: ExecutionPlan.PropertyGenerator[_]): Map[String, (String,String)] = {
    Map.empty
  }

  override def visit(node: ExecutionPlan.StructureGenerator): Map[String, (String,String)] = {
    Map.empty
  }

  override def visit(node: ExecutionPlan.PropertyTable[_]): Map[String, (String,String)] =  {
    val classTypeName = mkGeneratedClassName(node.name)
    val classDeclaration = generatePGClassDefinition(classTypeName,node.generator)
    val dependants = node.generator.dependentPropertyTables.map( table => table.accept[Map[String,(String,String)]](this) ).
      foldLeft(Map[String,(String,String)]())( {case (acc, next) => acc ++ next} )
    dependants + (node.name -> (classTypeName,classDeclaration))
  }

  override def visit(node: ExecutionPlan.EdgeTable): Map[String, (String,String)] = {
    Map.empty
  }

  override def visit(node: ExecutionPlan.TableSize): Map[String, (String,String)] =  {
    Map.empty
  }

  override def visit(node: ExecutionPlan.Match): Map[String, (String,String)] =  {
    Map.empty
  }
}
