package org.dama.datasynth.executionplan

/**
  * Created by aprat on 31/03/17.
  * Represents an execution plan. Effectively, its like a tree of ExecutionPlanNodes.
  * It contains several node types such as Parameter, Task or Generator
  * Some nodes produce values. These are indicated by "Producer" traits.
  * For instance, a long parameter is a LongProducer, similar as the Task "TableSize".
  */
object ExecutionPlan {

  /******************************************************/
  /** Base class for all execution plan nodes          **/
  /******************************************************/

  sealed abstract class ExecutionPlanNode {
    def accept( visitor : ExecutionPlanVisitor )
  };

  /********************************************************************/
  /** Abstract classes used to represent the differen type sof nodes **/
  /********************************************************************/

  /** Represents a value **/
  abstract class Value[T] extends ExecutionPlanNode {
    type parameterType = T
  }

  /** Represents a value whose value is known at compile time */
  case class StaticValue[T]( value : T) extends Value[T] {
    override def toString() : String = s"[StaticValue[${value.getClass.getSimpleName}],${value}]"
    override def accept(visitor: ExecutionPlanVisitor) = visitor.visit(this)
  }

  /** Produces a Table **/
  abstract class Table extends ExecutionPlanNode

  /** Produces a Property Table **/
  abstract class PropertyTable[T] extends Table {
    type propertyType = T
  }

  /** Produces an Edge Tables **/
  abstract class EdgeTable extends Table

  /******************************************************/
  /** Generators                                       **/
  /******************************************************/

  /**
    * Case class representing a property generator
    * @param className The name of the generator
    * @param initParameters The sequence of init parameters of the generator
    */
  case class PropertyGenerator[T]( className : String,
                                initParameters : Seq[Value[_]],
                                dependentGenerators : Seq[PropertyGenerator[_]]) extends ExecutionPlanNode {
    type propertyType = T
    override def toString: String = s"[PropertyGenerator,$className]"
    override def accept(visitor: ExecutionPlanVisitor) = visitor.visit(this)
  }

  /**
    * Case class representing a generator
    * @param className The name of the generator
    * @param initParameters The sequence of init parameters of the generator
    */
  case class GraphGenerator( className : String, initParameters : Seq[Value[_]]) extends ExecutionPlanNode {
    override def toString: String = s"[GraphGenerator,$className]"
    override def accept(visitor: ExecutionPlanVisitor) = visitor.visit(this)
  }

  /******************************************************/
  /** Tasks                                            **/
  /******************************************************/

  /**
    * Represents a create property table operation
    * @param generator The property generator to create the table
    * @param size  The LongProducer to obtain the size of the table from
    */
  case class CreatePropertyTable[T]( typeName : String, propertyName : String, generator : PropertyGenerator[_], size : Value[Long] )
    extends PropertyTable[T] {
    override def toString: String = s"[CreatePropertyTable,$typeName.$propertyName]"
    override def accept( visitor : ExecutionPlanVisitor ) = visitor.visit(this)
  }

  /**
    * Represents a create edge table operation
    * @param generator The edge generator to create the table
    * @param size  The LongProducer to obtain the size of the table from
    */
  case class CreateEdgeTable( tableName : String, generator : GraphGenerator, size : Value[Long] )
    extends EdgeTable {
    override def toString: String = s"[CreateEdgeTable,$tableName]"
    override def accept( visitor : ExecutionPlanVisitor ) = visitor.visit(this)
  }

  /**
    * Represents a size of table operation
    * @param table The table to compute the size from
    */
  case class TableSize( table : Table )
    extends Value[Long] {
    override def toString: String = "[TableSize]"
    override def accept( visitor : ExecutionPlanVisitor ) = visitor.visit(this)
  }

  /**
    * Represents a match operation between a property table and a graph
    *
    * @param tableName The name of the produced table
    * @param propertyTable The property table to match
    * @param graph The graph to match
    */
  case class Match( tableName : String, propertyTable : PropertyTable[_], graph : EdgeTable )
  extends EdgeTable {
    override def toString: String = s"[Match,$tableName]"
    override def accept(visitor: ExecutionPlanVisitor) =  visitor.visit(this)
  }
}

