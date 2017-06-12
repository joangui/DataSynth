package org.dama.datasynth.runtime.spark

import java.net.{URL, URLClassLoader}

import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.sql.{Dataset, SparkSession}
import org.dama.datasynth.DataSynthConfig
import org.dama.datasynth.executionplan.ExecutionPlan
import org.dama.datasynth.runtime.spark.operators.{FetchRndGeneratorOperator, FetchTableOperator}
import org.dama.datasynth.runtime.spark.passes.{InjectRuntimeGenerators, RuntimePropertyGeneratorBuilder}


/**
  * Created by aprat on 6/04/17.
  */
class SparkRuntime(config : DataSynthConfig) {

  def run(executionPlan : Seq[ExecutionPlan.Table] ) = {

    val spark = SparkSession.builder().getOrCreate()

    // Generate temporal jar with runtime generators
    val generatorBuilder = new RuntimePropertyGeneratorBuilder(config)
    val jarFileName = config.driverWorkspaceDir+"/temp.jar"
    val classes = generatorBuilder.buildJar(jarFileName, executionPlan)

    // Add jar to classpath
    val urlCl = new URLClassLoader( Array[URL](new URL("file://"+jarFileName)), getClass.getClassLoader());
    val fs = FileSystem.get(spark.sparkContext.hadoopConfiguration)
    if(spark.sparkContext.master == "yarn") {
      fs.copyFromLocalFile(false, true, new Path("file://" + jarFileName), new Path("hdfs://" + jarFileName))
      spark.sparkContext.addJar("hdfs://"+jarFileName)
    } else {
      spark.sparkContext.addJar("file://"+jarFileName)
    }

    // Patch execution plan to replace old generators with new existing ones
    val injectRuntimeGenerators = new InjectRuntimeGenerators(classes)
    val modifiedExecutionPlan = injectRuntimeGenerators.run(executionPlan)

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

