package org.dama.datasynth.runtime.operators

import org.apache.spark.sql.Dataset
import org.dama.datasynth.executionplan.ExecutionPlan.Table
import org.dama.datasynth.executionplan.{ExecutionPlan, ExecutionPlanNonVoidVisitor, ExecutionPlanVoidVisitor, TableNonVoidVisitor}
import org.dama.datasynth.runtime.SparkRuntime

import scala.reflect.runtime.universe.{TypeTag, typeOf}
import scala.collection.mutable

/**
  * Created by aprat on 11/04/17.
  */
object FetchTableOperator {

  private object FetchTableVisitor extends TableNonVoidVisitor[Dataset[_]] {

    override def visit( node: ExecutionPlan.PropertyTable[_] ): Dataset[_] = {
      node match {
        case t: ExecutionPlan.PropertyTable[Boolean @unchecked] if typeOf[Boolean] =:= node.tag.tpe  => fetchPropertyTableHelper(SparkRuntime.booleanTables, t, PropertyTableOperator.boolean)
        case t: ExecutionPlan.PropertyTable[Int     @unchecked] if typeOf[Int] =:= node.tag.tpe => fetchPropertyTableHelper(SparkRuntime.intTables, t, PropertyTableOperator.int)
        case t: ExecutionPlan.PropertyTable[Long    @unchecked] if typeOf[Long] =:= node.tag.tpe => fetchPropertyTableHelper(SparkRuntime.longTables, t, PropertyTableOperator.long)
        case t: ExecutionPlan.PropertyTable[Float   @unchecked] if typeOf[Float] =:= node.tag.tpe => fetchPropertyTableHelper(SparkRuntime.floatTables, t, PropertyTableOperator.float)
        case t: ExecutionPlan.PropertyTable[Double  @unchecked] if typeOf[Double] =:= node.tag.tpe => fetchPropertyTableHelper(SparkRuntime.doubleTables, t, PropertyTableOperator.double)
        case t: ExecutionPlan.PropertyTable[String  @unchecked] if typeOf[String] =:= node.tag.tpe => fetchPropertyTableHelper(SparkRuntime.stringTables, t, PropertyTableOperator.string)
      }
    }

    override def visit(node: ExecutionPlan.EdgeTable): Dataset[_] = ???
    override def visit(node: ExecutionPlan.Match): Dataset[_] = ???

  }

  /**
    * Fetches a property table from a given map or creates it
    *
    * @param map The map where the property table should be stored, and will be stored if created
    * @param node The execution plan node representing a PropertyTable
    * @param f The function used to create the property table given the execution plan node
    * @tparam T The type of the property table
    * @return The created PropertyTable as a spark dataset
    */
  private def fetchPropertyTableHelper[T]( map : mutable.HashMap[String,Dataset[(Long,T)]],
                        node : ExecutionPlan.PropertyTable[T],
                        f : (ExecutionPlan.PropertyTable[T]) => Dataset[(Long,T)] ) : Dataset[(Long,T)] = {
    map.get(node.name) match {
      case Some(t) => t
      case none => {
        val table = f(node)
        map.put(node.name, table)
        table
      }
    }
  }

  /**
    * Fetches a the table represented by the Table execution plan node
    *
    * @param table The execution plan node representing the table
    * @return The spark Dataset representing the fetched table
    */
  def apply(table : Table ): Dataset[_] =  table.accept[Dataset[_]](FetchTableVisitor)
}
