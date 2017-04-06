package org.dama.datasynth.executionplan

import org.dama.datasynth.executionplan.ExecutionPlan._

/**
  * Created by aprat on 4/04/17.
  *
  * Prints an execution plan to the screen
  */
class ExecutionPlanPrinter extends ExecutionPlanVisitor {

  var numIndents : Int = 0
  var indentString : String = "    "
  var stringBuilder : StringBuilder = new StringBuilder();

  def printExecutionPlan( node : ExecutionPlanNode ): String = {
    node.accept(this);
    stringBuilder.mkString
  }


  private def spaces( numIndents : Int ) : String = {
    numIndents match {
      case 1 => ""
      case _ => indentString ++: spaces(numIndents-1)
    }
  }

  private def printstring( str : String ) = {
    stringBuilder.append(spaces(numIndents)+str+"\n")
  }

  override def visit(node: Parameter) = {
    numIndents+=1
    printstring(node.toString)
    numIndents-=1
  }

  override def visit(node: PropertyGenerator) = {
    numIndents+=1
    printstring(node.toString)
    node.initParameters.foreach( p => p.accept(this))
    node.dependentGenerators.foreach( g => g.accept(this))
    numIndents-=1
  }

  override def visit(node: GraphGenerator) = {
    numIndents+=1
    printstring(node.toString)
    numIndents-=1
  }

  override def visit(node: CreatePropertyTable) = {
    numIndents+=1
    printstring(node.toString)
    node.size.accept(this)
    node.generator.accept(this)
    numIndents-=1
  }

  override def visit(node: CreateEdgeTable) = {
    numIndents+=1
    printstring(node.toString)
    node.size.accept(this)
    node.generator.accept(this)
    numIndents-=1
  }

  override def visit(node: TableSize) = {
    numIndents+=1
    printstring(node.toString)
    node.table.accept(this)
    numIndents-=1
  }
}
