package org.dama.datasynth.executionplan

import org.dama.datasynth.executionplan.ExecutionPlan._

/**
  * Created by aprat on 4/04/17.
  */
class ExecutionPlanPrinter extends ExecutionPlanVisitor {

  var numIndents : Int = 0

  override def visit(node: Parameter) = {
  }

  override def visit(node: PropertyGenerator) = {
  }

  override def visit(node: GraphGenerator): Unit = {
  }

  override def visit(node: CreatePropertyTable): Unit = {
  }

  override def visit(node: CreateEdgeTable): Unit = {
  }

  override def visit(node: TableSize): Unit = {
  }
}
