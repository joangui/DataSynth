package org.dama.datasynth.runtime.spark.operators

import org.apache.spark.sql.SparkSession
import org.dama.datasynth.executionplan._
import org.dama.datasynth.common.generators._
import org.dama.datasynth.runtime.spark.SparkRuntime

import scala.util.{Failure, Success}

/**
  * Created by aprat on 9/04/17.
  *
  * Operator to instantiate structure generators
  */
class InstantiateStructureGeneratorOperator {

  /**
    * Instantiates a structure generator
    * @param info The execution plan node representing the structure generator
    * @return The instantiated structure generator
    */
  def apply(info : ExecutionPlan.StructureGenerator) : structure.StructureGenerator = {
    structure.StructureGenerator.getInstance(info.className) match {
      case Success(generator) => {
        val initParameters: Seq[Any] = info.initParameters.map(x => SparkRuntime.evalValueOperator(x))
        generator.initialize(initParameters: _*)
        generator
      }
      case Failure(e) => throw e
    }
  }
}
