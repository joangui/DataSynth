package org.dama.datasynth.runtime.spark.operators

import org.apache.spark.sql.{Dataset, SparkSession}
import org.dama.datasynth.executionplan.ExecutionPlan.{PropertyTable, Table}
import org.dama.datasynth.executionplan.{ExecutionPlan, ExecutionPlanNonVoidVisitor, ExecutionPlanVoidVisitor, TableNonVoidVisitor}
import org.dama.datasynth.runtime.spark.SparkRuntime

import scala.reflect.runtime.universe.{TypeTag, typeOf}
import scala.collection.mutable

/**
  * Created by aprat on 11/04/17.
  *
  * Operator that fetches the spark datasets corresponding to tables, both property tables and edge tables
  * If the dataset has not been previously generated, it is generated
  *
  */
class FetchTableOperator {

  // Maps used to store the property tables per type
  val booleanTables = new mutable.HashMap[String, Dataset[(Long,Boolean)]]
  val intTables     = new mutable.HashMap[String, Dataset[(Long,Int)]]
  val longTables    = new mutable.HashMap[String, Dataset[(Long,Long)]]
  val floatTables   = new mutable.HashMap[String, Dataset[(Long,Float)]]
  val doubleTables  = new mutable.HashMap[String, Dataset[(Long,Double)]]
  val stringTables  = new mutable.HashMap[String, Dataset[(Long,String )]]

  // Map used to store the edge tables
  val edgeTables = new mutable.HashMap[String,Dataset[(Long,Long,Long)]]



  /**
    * Fetches a property table from a given map or creates it
    *
    * @tparam T The type of the property table
    * @return The created PropertyTable as a spark dataset
    */
  def getDataset[T](table: ExecutionPlan.PropertyTable[T])  : Dataset[(Long,T)]= {


    def fetchPropertyTableHelper[T2](map: mutable.HashMap[String, Dataset[(Long, T2)]],
                                     node: ExecutionPlan.PropertyTable[T2],
                                     f: (ExecutionPlan.PropertyTable[T2]) => Dataset[(Long, T2)]): Dataset[(Long, T2)] = {
      map.get(node.name) match {
        case Some(t) => t
        case none => {
          val table = f(node)
          map.put(node.name, table)
          table
        }
      }
    }


    val d=table match {
      case t: ExecutionPlan.PropertyTable[Boolean@unchecked] if typeOf[Boolean] =:= table.tag.tpe => fetchPropertyTableHelper(booleanTables, t, SparkRuntime.propertyTableOperator.boolean)
      case t: ExecutionPlan.PropertyTable[Int@unchecked] if typeOf[Int] =:= table.tag.tpe => fetchPropertyTableHelper(intTables, t, SparkRuntime.propertyTableOperator.int)
      case t: ExecutionPlan.PropertyTable[Long@unchecked] if typeOf[Long] =:= table.tag.tpe => fetchPropertyTableHelper(longTables, t, SparkRuntime.propertyTableOperator.long)
      case t: ExecutionPlan.PropertyTable[Float@unchecked] if typeOf[Float] =:= table.tag.tpe => fetchPropertyTableHelper(floatTables, t, SparkRuntime.propertyTableOperator.float)
      case t: ExecutionPlan.PropertyTable[Double@unchecked] if typeOf[Double] =:= table.tag.tpe => fetchPropertyTableHelper(doubleTables, t, SparkRuntime.propertyTableOperator.double)
      case t: ExecutionPlan.PropertyTable[String@unchecked] if typeOf[String] =:= table.tag.tpe => fetchPropertyTableHelper(stringTables, t, SparkRuntime.propertyTableOperator.string)
    }

    d.asInstanceOf[Dataset[(Long,T)]]
  }

  private class FetchTableVisitor() extends TableNonVoidVisitor[Dataset[_]] {


    override def visit(node: ExecutionPlan.PropertyTable[_]): Dataset[_] = {
      getDataset(node)
    }

    override def visit(node: ExecutionPlan.EdgeTable): Dataset[(Long, Long, Long)] = {
      edgeTables.get(node.name) match {
        case Some(t) => t
        case None => {
          val table = SparkRuntime.edgeTableOperator(node)
          edgeTables.put(node.name, table)
          table
        }
      }
    }

    override def visit(node: ExecutionPlan.MatchNode[_]): Dataset[_] = {
      throw new RuntimeException("visit over ExecutionPlan.Match not implemented")
    }
    override def visit(node: ExecutionPlan.BipartiteMatchNode[_,_]): Dataset[_] = {
      throw new RuntimeException("visit over ExecutionPlan.Match not implemented")
    }

  }

  /**
    * Fetches a the table represented by the Table execution plan node
    *
    * @param table The execution plan node representing the table
    * @return The spark Dataset representing the fetched table
    */
  def apply( table : Table ): Dataset[_] =  table.accept[Dataset[_]](new FetchTableVisitor())

  def clear(): Unit = {

    booleanTables.clear()
    intTables.clear()
    longTables.clear()
    floatTables.clear()
    doubleTables.clear()
    stringTables.clear()
    edgeTables.clear()

  }
}
