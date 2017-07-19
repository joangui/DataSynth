package org.dama.datasynth.runtime.spark.operators.matching.models.stochastic

import org.apache.spark.sql.{DataFrame, Dataset, Row}
import org.dama.datasynth.executionplan.ExecutionPlan.PropertyTable
import org.dama.datasynth.runtime.spark.SparkRuntime
import org.dama.datasynth.runtime.spark.operators.matching.Tuple
import org.dama.datasynth.runtime.spark.operators.matching.utils.JointDistribution

import scala.collection.{breakOut, immutable, mutable}
/**
  * Created by joangui on 10/07/2017.
  */

private object StochasticBlockModel
{
  def apply[T <% Ordered[T]](probabilities:immutable.HashMap[(T,T),Double],mapping:immutable.HashMap[T,Long]): StochasticBlockModel[T] ={
    new StochasticBlockModel(probabilities,mapping)
  }

  def extractForm[T <% Ordered[T]](numEdges : Long, propertyTable:PropertyTable[T],jointDistribution: JointDistribution[T,T]) = {
    val dataset: Dataset[(Long,T)] = SparkRuntime.fetchTableOperator.getDataset[T](propertyTable)
    val df: DataFrame = dataset.toDF("id","value")
    val mappingDataFrame:DataFrame = df.groupBy("value").count()


    val localData: Array[Row] = mappingDataFrame.collect()
    val valueList = localData.map(_.getAs[T]("value"))
    val countList = localData.map(_.getAs[Long]("count"))

    val mapping:immutable.HashMap[T,Long] = (valueList zip countList)(breakOut) : immutable.HashMap[T,Long]

    val probabilitiesMutable: mutable.HashMap[(T, T), Double] = mapping.foldLeft(mutable.HashMap[(T,T),Double]()) (
      {case (probabilitiesTMP,(value1,count1)) => {
        mapping.foreach({case (value2,count2)=>{
          val probability: Double = jointDistribution.getProbability(new Tuple[T,T](value1,value2))

          if (value1.equals(value2))
            probabilitiesTMP += (value1,value2)->2D*numEdges*probability/(count1*count2-1)
          else
            probabilitiesTMP += (value1,value2)->numEdges*probability/(count1*count2)
        }})
        probabilitiesTMP
      }})

    val probabilities: immutable.HashMap[(T, T), Double] = immutable.HashMap[(T,T),Double](probabilitiesMutable.toSeq:_*)
    new StochasticBlockModel(probabilities , mapping)
  }
}


//class StochasticBlockModel[T] private (probabilities:Array[Array[Double]],sizes:Array[Long],mapping:mutable.HashMap[T,Int]){

class StochasticBlockModel [T <% Ordered[T]] (probabilities : immutable.HashMap[(T,T),Double],mapping:immutable.HashMap[T,Long]){
  def getNumBlocks():Int = mapping.size

  def getSize(value:T):Long = {
    val size:Long=mapping.get(value).getOrElse(-1)
    if (size == -1)
      throw new RuntimeException("The stochastic block model is wrong. THIS SHOULD NEVER HAPPEN DUE TO PREVIOUS CHECKS.")
    size
  }

  def getProbability(value1:T, value2:T):Double={
    val probabilityOption: Option[Double] =
      if(value1 == value2 ) {
      probabilities.get((value1, value2))
    }
    else
    {
      val newValue1:T = if(value1 < value2) value1 else value2
      val newValue2:T = if(value2 > value1) value2 else value1
      probabilities.get((newValue1,newValue2))
    }

    probabilityOption match{
      case Some(probability) => probability
      case None => throw new RuntimeException(s"The stochastic block model is wrong. There is no probability" +
        s" for values $value1 and $value2")
    }
  }

  def getMapping():immutable.HashMap[(T, T), Double] = probabilities
  def getSizes():Array[Long] = mapping.values.toArray
}

