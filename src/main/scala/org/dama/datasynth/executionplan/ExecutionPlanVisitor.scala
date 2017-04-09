package org.dama.datasynth.executionplan

import org.dama.datasynth.executionplan.ExecutionPlan._

/**
  * Created by aprat on 4/04/17.
  *
  * Visitor abstract class for Execution Plans, used to implement printers, semantic analyses or optimization passes
  */
abstract class ExecutionPlanVisitor {
  def visit( node : StaticValue[_] )
  def visit( node : PropertyGenerator[_] )
  def visit( node : GraphGenerator )
  def visit( node : CreatePropertyTable[_] )
  def visit( node : CreateEdgeTable )
  def visit( node : TableSize )
  def visit( node : Match )
}
