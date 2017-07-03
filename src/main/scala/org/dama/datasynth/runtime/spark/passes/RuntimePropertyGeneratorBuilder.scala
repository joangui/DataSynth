package org.dama.datasynth.runtime.spark.passes

import java.io.{File, PrintWriter}

import org.dama.datasynth.DataSynthConfig
import org.dama.datasynth.executionplan.ExecutionPlan.{PropertyGenerator, PropertyTable, StaticValue, Value}
import org.dama.datasynth.executionplan.{ExecutionPlan, ExecutionPlanNonVoidVisitor, ExecutionPlanVoidVisitor}
import org.dama.datasynth.runtime.spark.{RuntimeClass, RuntimeClasses}
import org.dama.datasynth.runtime.spark.operators.{EvalValueOperator, FetchRndGeneratorOperator}

import scala.tools.nsc.io.{JManifest, Jar, JarWriter}
import scala.tools.nsc.{GenericRunnerSettings, Global, Settings}
import scala.reflect.runtime.universe.typeOf

/**
  * Created by aprat on 23/05/17.
  * This Visitor processes an execution plan and for each property generator, it generates its PropertyGenerator
  * Interface coutnerpart, compiles it and packages all of them into a jar
  */
class RuntimePropertyGeneratorBuilder(config : DataSynthConfig) extends ExecutionPlanNonVoidVisitor[RuntimeClasses] {


  def codePropertyTableClasses(executionPlan:Seq[ExecutionPlan.Table]): RuntimeClasses={
    /** Writing .scala files **/
  /*  val classes:Map[String,(String,String)] = executionPlan.foldLeft(
      Map[String,(String,String)]())( (currentDeclarations, nextNode) => currentDeclarations ++ nextNode.accept(this))
    classes
*/
    val classes2:RuntimeClasses=executionPlan.foldLeft(new RuntimeClasses)((codeClassses, nextNode)=>codeClassses++nextNode.accept(this))
    classes2

  }

  /**
    *
    * @param jarFileName Name of the jar file to be generated
    * @param classes A map with the filename and its code.
    */
  def buildJar(jarFileName:String, classes:Map[String,String]):Unit={

    //Prepare system to be capable of compiling classes
    val settings = new GenericRunnerSettings(println _)
    settings.nc.value = true
    settings.usejavacp.value = true
    settings.outputDirs.setSingleOutput(s"${config.driverWorkspaceDir}")
    val currentJarPath : String = getClass().getProtectionDomain().getCodeSource().getLocation().getPath()
    println(currentJarPath)
    settings.classpath.append(currentJarPath)
    val g = new Global(settings)
    val run = new g.Run




    val sourceFileNames : List[String] = classes.toList.map({case (className,classCode)=>{
    val fileName:String = s"${config.driverWorkspaceDir}/$className.scala"
    val writer = new PrintWriter(new java.io.File(fileName))
    writer.write(classCode)
    writer.close()

    fileName
    }})

    run.compile(sourceFileNames)

    /** Building JAR **/
    val jar : JarWriter = new JarWriter(new scala.reflect.io.File(new java.io.File(jarFileName)), new JManifest())
    sourceFileNames.map( file => file.replace(".scala",".class")).foreach( file => jar.addFile(new scala.reflect.io.File( new java.io.File(file)),""))
    jar.close()
  }




  /**
    * Generates the name of a generated proeprty generator given an original generator name
    * @param name The name of the original property generator
    * @return The new name for the generated property generator
    */
  private def generateClassName( name : String ): String = {
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
    * @param propertyGenerator The execution plan node representing the property generated to generate the new generator from
    * @tparam T The data type returned by the given property generator
    * @return  The class declaration
    */
  private[passes] def generatePGClassDefinition[T](className : String, propertyGenerator : PropertyGenerator[T]) : String = {
    val returnTypeName = propertyGenerator.tag.tpe.toString
    val initParameters : String = if(propertyGenerator.initParameters.length == 0) {
      " "
    } else {
      propertyGenerator.initParameters.map({
        case parameter: StaticValue[String@unchecked] if parameter.tag.tpe =:= typeOf[String] => s"""\"${parameter.value.toString}\""""
        case parameter: StaticValue[Float@unchecked] if parameter.tag.tpe =:= typeOf[Float] => s"${parameter.value.toString}f"
        case parameter: StaticValue[_] => s"${parameter.value.toString}"
        case _ => throw new RuntimeException("Unable to evaluate initialiation parameter. Is this a StaticValue?")
      })
      .reduceLeft((current,next) => current+","+next )
    }
    val propertyGeneratorName:String = propertyGenerator.className
    val generatedClassName:String = generateClassName(className)
   // val dependentGenerators = propertyGenerator.dependentPropertyTables.map(table => generateClassName(table.name))
    val dependentGeneratorsCallList : String = propertyGenerator.dependentPropertyTables.zipWithIndex.foldLeft("")(
      {case (current,(table,index)) => s"$current,${mkMatch(table,index)}"})

    s"import org.dama.datasynth.runtime.spark.utils._\n" +
    s"import org.dama.datasynth.common.generators.property._\n" +
    s"class $generatedClassName extends PropertyGenerator[$returnTypeName] {\n" +
    s"val generator$generatedClassName = new $propertyGeneratorName($initParameters)\n"+
    s" def run(id : Long, random : Long, dependencies : Any*) : $returnTypeName = generator$generatedClassName.run(id,random$dependentGeneratorsCallList)\n" +
    s"}\n"
  }

  override def visit(node: ExecutionPlan.StaticValue[_]): RuntimeClasses =  {
//    Map.empty
    throw new RuntimeException("No code should be generated for StaticValue[_]")
  }

  override def visit(node: ExecutionPlan.PropertyGenerator[_]): RuntimeClasses =  {
    //    Map.empty
    throw new RuntimeException("No code should be generated for PropertyGenerator[_]")
  }

  override def visit(node: ExecutionPlan.StructureGenerator): RuntimeClasses =  {
    //    Map.empty
    throw new RuntimeException("No code should be generated for StructureGenerator")
  }

  /*override def visit(node: ExecutionPlan.PropertyTable[_]): Map[String, (String,String)] =  {
    val classTypeName = generateClassName(node.name)
    val classDeclaration = generatePGClassDefinition(classTypeName,node.generator)
    val dependants = node.generator.dependentPropertyTables.map( table => table.accept[Map[String,(String,String)]](this) ).
      foldLeft(Map[String,(String,String)]())( {case (accumulated, next) => accumulated ++ next} )
    dependants + (node.name -> (classTypeName,classDeclaration))

  }*/

  override def visit(node: ExecutionPlan.PropertyTable[_]): RuntimeClasses =  {
    val classTypeName = generateClassName(node.name)
    val classDeclaration = generatePGClassDefinition(classTypeName,node.generator)


    val dependants:RuntimeClasses = node.generator.dependentPropertyTables.map(table => table.accept[RuntimeClasses](this) ).
      foldLeft(new RuntimeClasses())( {case (accumulated, next) => accumulated ++ next} )
    val codeClass : RuntimeClass = new RuntimeClass(node.name,classTypeName,classDeclaration)
    dependants + codeClass
dependants
  }


  override def visit(node: ExecutionPlan.EdgeTable): RuntimeClasses =  {
    //    Map.empty
    throw new RuntimeException("No code should be generated for EdgeTable")
  }

  override def visit(node: ExecutionPlan.TableSize): RuntimeClasses =  {
    //    Map.empty
    throw new RuntimeException("No code should be generated for TableSize")
  }

  override def visit(node: ExecutionPlan.Match): RuntimeClasses =  {
    //    Map.empty
    throw new RuntimeException("No code should be generated for Match")
  }
}
