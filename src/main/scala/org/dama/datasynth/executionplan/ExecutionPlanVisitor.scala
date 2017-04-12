package org.dama.datasynth.executionplan

import org.dama.datasynth.executionplan.ExecutionPlan._

/**
  * Created by aprat on 4/04/17.
  *
  * Visitor abstract class for Execution Plans, used to implement printers, semantic analyses or optimization passes
  */


abstract class ExecutionPlanVisitor {

  protected val msg:String="The usage of this type of visitor is no supported for the current ExecutionPlanNode.";

  def visit(node: PropertyTable[_]) //Table
  def visit(node: EdgeTable) //Table
  def visit(node: Match) //Table
  def visit( node : PropertyGenerator[_] ) //Generator
  def visit( node : GraphGenerator ) //Generator
  def visit( node : StaticValue[_] ) //Value
  def visit( node : TableSize) //Table
}


abstract class TableVisitor extends ExecutionPlanVisitor{
  def visit( node : PropertyGenerator[_] ) = new RuntimeException(msg);//Generator
  def visit( node : GraphGenerator )  = new RuntimeException(msg);//Generator
  def visit( node : StaticValue[_] )  = new RuntimeException(msg);//Value
  def visit( node : TableSize)  = new RuntimeException(msg);//Table
}

abstract class PropertyGeneratorVisitor extends ExecutionPlanVisitor {

  def visit(node: PropertyTable[_])  = new RuntimeException(msg);//Table
  def visit(node: EdgeTable)  = new RuntimeException(msg);//Table
  def visit(node: Match)  = new RuntimeException(msg);//Table
  def visit( node : GraphGenerator )  = new RuntimeException(msg);//Generator
  def visit( node : StaticValue[_] )  = new RuntimeException(msg);//Value
  def visit( node : TableSize)  = new RuntimeException(msg);//Table
}

abstract class GraphGeneratorVisitor  extends ExecutionPlanVisitor {

  def visit(node: PropertyTable[_]) = new RuntimeException(msg); //Table
  def visit(node: EdgeTable) = new RuntimeException(msg); //Table
  def visit(node: Match)  = new RuntimeException(msg);//Table
  def visit( node : PropertyGenerator[_] )  = new RuntimeException(msg);//Generator
  def visit( node : StaticValue[_] )  = new RuntimeException(msg);//Value
  def visit( node : TableSize)  = new RuntimeException(msg);//Table
}

abstract class ValueVisitor  extends ExecutionPlanVisitor {
  def visit(node: PropertyTable[_])  = new RuntimeException(msg);//Table
  def visit(node: EdgeTable)  = new RuntimeException(msg);//Table
  def visit(node: Match)  = new RuntimeException(msg);//Table
  def visit( node : PropertyGenerator[_] )  = new RuntimeException(msg);//Generator
  def visit( node : GraphGenerator )  = new RuntimeException(msg);//Generator
  def visit( node : TableSize)  = new RuntimeException(msg);//Table
}
