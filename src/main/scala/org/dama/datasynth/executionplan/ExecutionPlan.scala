package org.dama.datasynth.executionplan

/**
  * Created by aprat on 31/03/17.
  */
object ExecutionPlan {

  /******************************************************/
  /** Traits used to represent what each task produces **/
  /******************************************************/

  /** Produces a Long **/
  trait LongProducer

  /** Produces a String **/
  trait StringProducer

  /** Produces a Table **/
  trait TableProducer

  /** Produces a Property Table **/
  trait PropertyTableProducer extends TableProducer

  /** Produces an Edge Tables **/
  trait EdgeTableProducer extends TableProducer

  /******************************************************/
  /** Parameters provided by users                     **/
  /******************************************************/

  abstract class Parameter

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
  case class PropertyGenerator( className : String, initParameters : Seq[Parameter], dependantGenerators : Seq[PropertyGenerator]) {
    override def toString: String = s"[PropertyGenerator,$className]"
  }

  /**
    * Case class representing a generator
    * @param className The name of the generator
    * @param initParameters The sequence of init parameters of the generator
    */
  case class GraphGenerator( className : String, initParameters : Seq[Parameter]) {
    override def toString: String = s"[GraphGenerator,$className]"
  }

  /******************************************************/
  /** Tasks                                            **/
  /******************************************************/

  /**
    * Represents a task in the execution plan
    */
  sealed abstract class Task()

  /**
    * Represents a create property table operation
    * @param generator The property generator to create the table
    * @param size  The LongProducer to obtain the size of the table from
    */
  case class CreatePropertyTable( tableName : String, generator : PropertyGenerator, size : LongProducer )
    extends Task
    with PropertyTableProducer {
    override def toString: String = s"[CreatePropertyTable,$tableName]"
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
  }

  /**
    * Represents a size of table operation
    * @param table The table to compute the size from
    */
  case class TableSize( table : TableProducer )
    extends Task {
    override def toString: String = "[TableSize]"
  }

}
