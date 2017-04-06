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

  /******************************************************/
  /** Traits used to represent what each task produces **/
  /******************************************************/

  /** Produces a Long **/
  trait LongProducer

  /** Produces a String **/
  trait StringProducer

  /** Produces a Table **/
  trait TableProducer;

  /** Produces a Property Table **/
  trait PropertyTableProducer extends TableProducer

  /** Produces an Edge Tables **/
  trait EdgeTableProducer extends TableProducer

  /******************************************************/
  /** Parameters provided by users                     **/
  /******************************************************/

  sealed abstract class Parameter extends ExecutionPlanNode {
    override def accept(visitor: ExecutionPlanVisitor) = visitor.visit(this)
  }

  /**
    * Represents a parameter provided by the user of type Long
    * @param value The value of the parameter
    */
  case class LongParameter( value : Long )
    extends Parameter
    with LongProducer {
    override def toString: String = s"[LongParameter,$value]"
  }

  /**
    * Represents a parameter provided by the user of type Long
    * @param value The value of the parameter
    */
  case class StringParameter( value : String )
    extends Parameter
    with StringProducer {

    override def toString: String = s"[StringParameter,$value]"
  }

  /******************************************************/
  /** Generators                                       **/
  /******************************************************/

  /**
    * Case class representing a property generator
    * @param className The name of the generator
    * @param initParameters The sequence of init parameters of the generator
    */
  case class PropertyGenerator( className : String,
                                initParameters : Seq[Parameter],
                                dependentGenerators : Seq[PropertyGenerator]) extends ExecutionPlanNode {

    def this(className : String ) = this(className, Seq[Parameter](), Seq[PropertyGenerator]())
    //def this(className : String, initParameters : Seq[Parameter] ) = this(className, initParameters, Seq[PropertyGenerator]())
    def this(className : String, dependentGenerators : Seq[PropertyGenerator] ) = this(className, Seq[Parameter](), dependentGenerators )

    override def toString: String = s"[PropertyGenerator,$className]"
    override def accept(visitor: ExecutionPlanVisitor) = visitor.visit(this)
  }

  /**
    * Case class representing a generator
    * @param className The name of the generator
    * @param initParameters The sequence of init parameters of the generator
    */
  case class GraphGenerator( className : String, initParameters : Seq[Parameter]) extends ExecutionPlanNode {
    override def toString: String = s"[GraphGenerator,$className]"
    override def accept(visitor: ExecutionPlanVisitor) = visitor.visit(this)
  }

  /******************************************************/
  /** Tasks                                            **/
  /******************************************************/

  /**
    * Represents a task in the execution plan
    */
  sealed abstract class Task extends ExecutionPlanNode

  /**
    * Represents a create property table operation
    * @param generator The property generator to create the table
    * @param size  The LongProducer to obtain the size of the table from
    */
  case class CreatePropertyTable( tableName : String, generator : PropertyGenerator, size : LongProducer )
    extends Task
    with PropertyTableProducer {
    override def toString: String = s"[CreatePropertyTable,$tableName]"
    override def accept( visitor : ExecutionPlanVisitor ) = visitor.visit(this)
  }

  /**
    * Represents a create edge table operation
    * @param generator The edge generator to create the table
    * @param size  The LongProducer to obtain the size of the table from
    */
  case class CreateEdgeTable( tableName : String, generator : GraphGenerator, size : LongProducer )
    extends  Task
    with EdgeTableProducer {
    override def toString: String = s"[CreateEdgeTable,$tableName]"
    override def accept( visitor : ExecutionPlanVisitor ) = visitor.visit(this)
  }

  /**
    * Represents a size of table operation
    * @param table The table to compute the size from
    */
  case class TableSize( table : TableProducer )
    extends Task {
    override def toString: String = s"[TableSize]"
    override def accept( visitor : ExecutionPlanVisitor ) = visitor.visit(this)
  }

}
