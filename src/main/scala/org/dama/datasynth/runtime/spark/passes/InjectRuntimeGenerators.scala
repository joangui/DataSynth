package org.dama.datasynth.runtime.spark.passes

import org.dama.datasynth.executionplan.ExecutionPlan.{EdgeTable, PropertyGenerator, PropertyTable}
import org.dama.datasynth.executionplan.{ExecutionPlan, ExecutionPlanNonVoidVisitor}

import scala.reflect.runtime.universe.{TypeTag, typeOf}

/**
  * Created by aprat on 23/05/17.
  * Visitor that replaces existing property generators by their automatically generated counterparts
  */
class InjectRuntimeGenerators( classes : Map[String, String]) extends ExecutionPlanNonVoidVisitor[Either[PropertyTable[_],PropertyGenerator[_]]]{

  /**
    * Given an execution plan, replaces the existing property generators in this plan for
    * their automatically generated counterparts
    * @param executionPlan The execution plan to process
    * @return The new execution plan
    */
  def run( executionPlan : Seq[ExecutionPlan.Table] ): Seq[ExecutionPlan.Table] = {
    executionPlan.map({ case ptable : PropertyTable[_] => ptable.accept(this) match {
                                                               case Left(table) => table
                                                               case Right(generator) => throw new RuntimeException("Ill-formed execution plan visitor")
                        }
                        case etable : EdgeTable => etable})
  }

  /**
    * Patches a property generator
    * @param node The execution plan node representing the property generator
    * @tparam T The type of the return type of the property generator
    * @return The new patched property generator
    */
  private[passes] def patchPropertyGenerator[T : TypeTag]( tableName : String, node : PropertyGenerator[T]): PropertyGenerator[T] = {
    val dependentPropertyTables = node.dependentPropertyTables.map( table => table.accept(this) match {
      case Left(table) => table
      case Right(generator) => throw new RuntimeException("Ill-formed execution plan visitor")
    })
    val newClassName = classes.get(tableName) match {
      case Some(className) => className
      case None => throw new RuntimeException(s"Missing runtime generated property generator for property generator ${node.className}")
    }
    new PropertyGenerator[T](newClassName,Seq(),dependentPropertyTables)
  }

  /**
    * Patches a property table
    * @param node The execution plan node representing the property table
    * @tparam T The type of the property table
    * @return The new patched property table
    */
  def patchPropertyTable[T : TypeTag]( node : PropertyTable[T]): PropertyTable[T] = {
    val generator = patchPropertyGenerator[T](node.name, node.generator)
    new PropertyTable[T](node.typeName,node.propertyName,generator,node.size)
  }

  override def visit(node: PropertyGenerator[_]): Either[PropertyTable[_],PropertyGenerator[_]] = {
    throw new RuntimeException("Visit on property generator should not be called")
  }

  override def visit(node: ExecutionPlan.PropertyTable[_]): Either[PropertyTable[_],PropertyGenerator[_]] =  {
    node match {
      case table : PropertyTable[Boolean@unchecked] if typeOf[Boolean] =:= node.tag.tpe => Left(patchPropertyTable[Boolean](table))
      case table : PropertyTable[Int@unchecked] if typeOf[Int] =:= node.tag.tpe => Left(patchPropertyTable[Int](table))
      case table : PropertyTable[Long@unchecked] if typeOf[Long] =:= node.tag.tpe => Left(patchPropertyTable[Long](table))
      case table : PropertyTable[Float@unchecked] if typeOf[Float] =:= node.tag.tpe => Left(patchPropertyTable[Float](table))
      case table : PropertyTable[Double@unchecked] if typeOf[Double] =:= node.tag.tpe => Left(patchPropertyTable[Double](table))
      case table : PropertyTable[String@unchecked] if typeOf[String] =:= node.tag.tpe => Left(patchPropertyTable[String](table))
    }
  }

  override def visit(node: ExecutionPlan.EdgeTable): Either[PropertyTable[_],PropertyGenerator[_]] = {
    throw new RuntimeException("Ill-formed execution plan visitor")
  }

  override def visit(node: ExecutionPlan.TableSize): Either[PropertyTable[_],PropertyGenerator[_]] = {
    throw new RuntimeException("Ill-formed execution plan visitor")
  }

  override def visit(node: ExecutionPlan.Match): Either[PropertyTable[_],PropertyGenerator[_]] = {
    throw new RuntimeException("Ill-formed execution plan visitor")
  }

  override def visit(node: ExecutionPlan.StaticValue[_]): Either[PropertyTable[_],PropertyGenerator[_]] = {
    throw new RuntimeException("Ill-formed execution plan visitor")
  }

  override def visit(node: ExecutionPlan.StructureGenerator): Either[PropertyTable[_],PropertyGenerator[_]] = {
    throw new RuntimeException("Ill-formed execution plan visitor")
  }
}
