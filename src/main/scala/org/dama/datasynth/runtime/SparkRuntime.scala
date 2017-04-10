package org.dama.datasynth.runtime

import org.dama.datasynth.executionplan.ExecutionPlan.Table
import org.dama.datasynth.executionplan.{ExecutionPlan, ExecutionPlanVisitor}

/**
  * Created by aprat on 6/04/17.
  */
object SparkRuntime extends ExecutionPlanVisitor {

  def run( executionPlan : List[Table] ) = {
    executionPlan.foreach( x => x.accept(this))
  }

  override def visit(node: ExecutionPlan.StaticValue[_]): Unit =  {

  }

  override def visit(node: ExecutionPlan.PropertyGenerator[_]) = {
  }

  override def visit(node: ExecutionPlan.GraphGenerator) = {

  }

  override def visit(node: ExecutionPlan.CreatePropertyTable[_]) = {

  }

  override def visit(node: ExecutionPlan.CreateEdgeTable) = {

  }

  override def visit(node: ExecutionPlan.TableSize) = {

  }

  override def visit(node: ExecutionPlan.Match) = {

  }

}
