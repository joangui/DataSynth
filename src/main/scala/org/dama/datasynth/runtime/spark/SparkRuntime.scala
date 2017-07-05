package org.dama.datasynth.runtime.spark

import java.net.{URL, URLClassLoader}

import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.sql.{Dataset, SparkSession}
import org.dama.datasynth.DataSynthConfig
import org.dama.datasynth.executionplan.ExecutionPlan
import org.dama.datasynth.runtime.spark.operators._
import org.dama.datasynth.runtime.spark.passes.{InjectRuntimeGenerators, RuntimePropertyGeneratorBuilder}

import scala.collection.mutable


/**
  * Created by aprat on 6/04/17.
  */
object SparkRuntime{

  private[spark] val edgeTableOperator = new EdgeTableOperator()
  private[spark] val fetchTableOperator = new FetchTableOperator()
  private[spark] val evalValueOperator = new EvalValueOperator()
  private[spark] val fetchRndGeneratorOperator = new FetchRndGeneratorOperator()
  private[spark] val instantiatePropertyGeneratorOperator  = new InstantiatePropertyGeneratorOperator()
  private[spark] val instantiateStructureGeneratorOperator = new InstantiateStructureGeneratorOperator()
  private[spark] val propertyTableOperator = new PropertyTableOperator()
  private[spark] val tableSizeOperator = new TableSizeOperator()

  var config : Option[DataSynthConfig]= None
  private[spark] var sparkSession : Option[SparkSession] = None

  def start(config:DataSynthConfig):Unit={
    this.config = Some(config)
    this.sparkSession = Some(SparkSession.builder().getOrCreate())
  }


  private[spark] def getSparkSession():SparkSession = {
    this.sparkSession match {
      case Some(s) => s
      case None => throw  new RuntimeException("SparkRuntime must be started.")
    }
  }

  private[spark] def getConfig(): DataSynthConfig = {
    this.config match {
      case Some(c) => c
      case None => throw  new RuntimeException("SparkRuntime must be started.")
    }
  }

  def run(executionPlan : Seq[ExecutionPlan.Table] ) = {

    val config:DataSynthConfig = getConfig()
    val sparkSession = getSparkSession()

    // Generate temporal jar with runtime generators
    val generatorBuilder = new RuntimePropertyGeneratorBuilder(config)
    val jarFileName:String = config.driverWorkspaceDir+"/temp.jar"
    //val classes = generatorBuilder.buildJar(jarFileName, executionPlan)


    val runtimeClasses : RuntimeClasses = generatorBuilder.codePropertyTableClasses(executionPlan)
    val runtimeCode:Map[String,String] = runtimeClasses.classNameToClassCode

    generatorBuilder.buildJar(jarFileName,runtimeCode)


    // Add jar to classpath
    val urlCl = new URLClassLoader( Array[URL](new URL("file://"+jarFileName)), getClass.getClassLoader());
    val fs:FileSystem = FileSystem.get(sparkSession.sparkContext.hadoopConfiguration)
    if(sparkSession.sparkContext.master == "yarn") {
      fs.copyFromLocalFile(false, true, new Path("file://" + jarFileName), new Path("hdfs://" + jarFileName))
      sparkSession.sparkContext.addJar("hdfs://"+jarFileName)
    } else {
      sparkSession.sparkContext.addJar("file://"+jarFileName)
    }

    // Patch execution plan to replace old generators with new existing ones
    val classesNames:mutable.Map[String,String] = runtimeClasses.propertyTableNameToClassName
    val injectRuntimeGenerators = new InjectRuntimeGenerators(classesNames.toMap)
    val modifiedExecutionPlan:Seq[ExecutionPlan.Table] = injectRuntimeGenerators.run(executionPlan)

    // Execute execution plan
    modifiedExecutionPlan.foreach(table =>
      fetchTableOperator(table).write.csv(config.outputDir+"/"+table.name)
    )
  }

  def stop(): Unit = {
    fetchTableOperator.clear()
    fetchRndGeneratorOperator.clear()

    sparkSession match {
      case Some(s) => s.stop()
      case None => throw  new RuntimeException("SparkRuntime must be started.")
    }
  }
}





