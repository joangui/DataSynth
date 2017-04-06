package org.dama.datasynth.executionplan

import org.dama.datasynth.executionplan.ExecutionPlan._

/**
  * Created by aprat on 4/04/17.
  *
  * Visitor abstract class for Execution Plans, used to implement printers, semantic analyses or optimization passes
  */
abstract class ExecutionPlanVisitor {
  def visit( node : Parameter )
  def visit( node : PropertyGenerator )
  def visit( node : GraphGenerator )
  def visit( node : CreatePropertyTable )
  def visit( node : CreateEdgeTable )
  def visit( node : TableSize )
}
