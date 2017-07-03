package org.dama.datasynth.runtime.spark

import java.net.{URL, URLClassLoader}
import java.util

import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.sql.{Dataset, SparkSession}
import org.dama.datasynth.DataSynthConfig
import org.dama.datasynth.executionplan.ExecutionPlan
import org.dama.datasynth.runtime.spark.operators.{FetchRndGeneratorOperator, FetchTableOperator}
import org.dama.datasynth.runtime.spark.passes.{InjectRuntimeGenerators, RuntimePropertyGeneratorBuilder}

import scala.collection.mutable


/**
  * Created by aprat on 6/04/17.
  */
class SparkRuntime(config : DataSynthConfig) {

  def run(executionPlan : Seq[ExecutionPlan.Table] ) = {

    val spark:SparkSession = SparkSession.builder().getOrCreate()

    // Generate temporal jar with runtime generators
    val generatorBuilder = new RuntimePropertyGeneratorBuilder(config)
    val jarFileName:String = config.driverWorkspaceDir+"/temp.jar"
    //val classes = generatorBuilder.buildJar(jarFileName, executionPlan)

   // val classes:CodeClasses =  new CodeClasses()
   // classes.add(generatorBuilder.codePropertyTableClasses(executionPlan))

    val classes : RuntimeClasses = generatorBuilder.codePropertyTableClasses(executionPlan)

    val classCode:Map[String,String] = classes.classNameToClassCode

    generatorBuilder.buildJar(jarFileName,classCode)


    // Add jar to classpath
    val urlCl = new URLClassLoader( Array[URL](new URL("file://"+jarFileName)), getClass.getClassLoader());
    val fs:FileSystem = FileSystem.get(spark.sparkContext.hadoopConfiguration)
    if(spark.sparkContext.master == "yarn") {
      fs.copyFromLocalFile(false, true, new Path("file://" + jarFileName), new Path("hdfs://" + jarFileName))
      spark.sparkContext.addJar("hdfs://"+jarFileName)
    } else {
      spark.sparkContext.addJar("file://"+jarFileName)
    }

    // Patch execution plan to replace old generators with new existing ones
    val classesNames:mutable.Map[String,String] = classes.propertyTableNameToClassName
    val injectRuntimeGenerators = new InjectRuntimeGenerators(classesNames.toMap)
    val modifiedExecutionPlan:Seq[ExecutionPlan.Table] = injectRuntimeGenerators.run(executionPlan)

    // Execute execution plan
    modifiedExecutionPlan.foreach(table =>
          FetchTableOperator(spark,table).write.csv(config.outputDir+"/"+table.name)
    )
  }

  def stop(): Unit = {
    FetchTableOperator.clear()
    FetchRndGeneratorOperator.clear()
    SparkSession.builder().getOrCreate().stop()
  }
}

class RuntimeClass(val propertyTableName:String, val className:String, val code:String)

class RuntimeClasses()
{
  val runtimeClasses =  scala.collection.mutable.Map[String,(String,String)]()

  def add(newClasses: Map[String,(String,String)]): Unit = {
    newClasses.foreach({case (entry)=>runtimeClasses+entry})
  }

  def  classNameToClassCode  : Map[String,String]= runtimeClasses.foldLeft(Map[String,String]())({case (classesCode,(_,(filename,filecode)))=>classesCode + (filename->filecode)})
  def propertyTableNameToClassName : mutable.Map[String, String]= runtimeClasses.map( { case (propertyTable,(className,_)) => (propertyTable -> className) })

  def addClass(codeClass:RuntimeClass)={runtimeClasses += codeClass.propertyTableName->(codeClass.className,codeClass.code)}
  def addClass(propertyTableName:String,className:String,codeClass:String):Unit={runtimeClasses+=propertyTableName->(className,codeClass)}

  def +(codeClass:RuntimeClass):RuntimeClasses={
    addClass(codeClass)
    this
  }

  def ++(codeClasses:RuntimeClasses):RuntimeClasses={
    runtimeClasses++=codeClasses.runtimeClasses
    this}

}



