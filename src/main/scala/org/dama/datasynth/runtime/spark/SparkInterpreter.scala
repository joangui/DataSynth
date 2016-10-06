package org.dama.datasynth.runtime.spark


import org.apache.spark.sql.catalyst.encoders.RowEncoder
import org.apache.spark.api.java.function.{MapFunction, MapPartitionsFunction}
import org.apache.spark.sql.expressions.MutableAggregationBuffer
import org.apache.spark.sql.types._
import org.dama.datasynth.schnappi.ast._
import org.apache.spark.sql.{Dataset, Row, SparkSession}
import org.dama.datasynth.DataSynthConfig
import org.dama.datasynth.common.Types
import org.dama.datasynth.runtime.ExecutionException
import org.dama.datasynth.runtime.spark.untyped.UntypedMethod
import org.dama.datasynth.utils.Tuple

import scala.collection.JavaConversions._
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
  * Created by aprat on 26/09/16.
  */
class SparkInterpreter( configuration : DataSynthConfig) extends Visitor[ExpressionValue] with Serializable{

  val config = configuration
  val name = "prova"
  val tables = new mutable.HashMap[Id,Table[Dataset[Row]]]
  val variables = new mutable.HashMap[Var,ExpressionValue]
  val spark = SparkSession.builder()
    .appName("Datasynth Spark Schnappi Interpreter")
    .master("local[4]")
    .getOrCreate()

  override def call ( ast : Ast) {
    for(operation <- ast.getOperations) operation.accept(this)
  }


  /** Visitor methods **/

  override def visit(n: Assign): ExpressionValue = {
    n.getId  match {
      case id : Id => {
        tables.put(id,n.getExpression.accept[ExpressionValue](this) match { case t: Table[Dataset[Row]] =>
          new Table[Dataset[Row]](t.getData.withColumnRenamed("",id.getValue.replace(".","_")))})
      }
      case variable : Var => {
        variables.put(variable,n.getExpression.accept[ExpressionValue](this))
      }
      case _ => throw  new ExecutionException("Invalid left value in assignment. Cannot assign anything to a literal")
    }
    return null
  }

  override def visit(n: Expression): Table[Dataset[Row]] = {
    throw new NotImplementedError;
  }

  override def visit(n: Function): ExpressionValue = {
    n.getName match {
      case "map" => return execMap(n)
      case "mappart" => return execMappart(n)
      case "join" =>  return execJoin(n)
      case "zip" =>  return execZip(n)
      case "spawn" => return execSpawn(n)
      case "init" => return execInit(n)
      case "sort" => return execSort(n)
      case "range" => return execRange(n)
      case "dump"  => return execDump(n)
      case _ => throw new ExecutionException("Unsupported method "+n.getName)
    }
  }

  override def visit(n: Atomic): ExpressionValue = {
    throw new NotImplementedError()
  }

  override def visit(n: Var): ExpressionValue = {
    variables.get(n).get;
  }

  override def visit(n: Id): ExpressionValue = {
    tables.get(n).get;
  }

  override def visit(n: StringLiteral): ExpressionValue = {
    new Literal(n);
  }

  override def visit(n: Number): ExpressionValue = new Literal(n)

  /** Function execution functions **/

  def execMap( f: Function) : Table[Dataset[Row]] = {
    val generator = getGenerator(f.getParameters.get(0).accept(this))
    val table = getTable(f.getParameters.get(1).accept(this))

    val function : MapFunction[Row,Row] =  getGeneratorRunFunction(generator,table)
    val schema = StructType(Seq(StructField("id",LongType), StructField("",getGeneratorRunReturnType(generator))))
    return new Table[Dataset[Row]](table.getData.map(function, RowEncoder(schema)))
  }

  def execMappart( f: Function) : Table[Dataset[Row]] = {
    val generator = getGenerator(f.getParameters.get(0).accept(this))
    val table = getTable(f.getParameters.get(1).accept(this))

    val function : MapPartitionsFunction[Row,Row] = getBlockGeneratorRunFunction(generator,table)
    val schema = StructType(Seq(StructField("tail",LongType),StructField("head",LongType)))
    return new Table[Dataset[Row]](table.getData.repartition(64).mapPartitions(function, RowEncoder(schema)))
  }

  def execInit( f: Function) : Generator = {
    // retrieves the generator name, which should be the first parameter of the parameter list
    val generatorName = getStringLiteral(f.getParameters.get(0).accept(this))
    // retrieves the list of parameters of the init function (all but the first one) in their object form.
    val initParameters = f.getParameters().toList.drop(1).map( x => x.accept(this) match {
      case l : Literal => l.getLiteral.getObject
      case _ => throw new ExecutionException("Parameter type in init function must be a literal")
    })

    val generator = Types.getGenerator(generatorName)
    // gets the init method.
    val m = new MethodSerializable(generator,"initialize",initParameters.map(x => Types.DataType.fromObject(x)),null)
    m.invoke(initParameters)
    new Generator(generator)
  }

  def execSort( f: Function) : Table[Dataset[Row]] = {
    val table = getTable(f.getParameters.get(0).accept(this))
    val index = getIntegerLiteral(f.getParameters.get(1).accept(this)) + 1
    new Table[Dataset[Row]](table.getData.sort(table.getData.columns(index)))
  }

  def execSpawn( f: Function) : Table[Dataset[Row]] = {

    // get the generator
    val generator = getGenerator(f.getParameters().get(0).accept(this))
    // get the number of elements to generate
    val numElements = getIntegerLiteral(f.getParameters.get(1).accept(this))

    val idsTable =  new Table[Dataset[Row]](spark.range(0,numElements).toDF())
    val function : MapFunction[Row,Row] = getGeneratorRunFunction(generator,idsTable)
    val schema = StructType(Seq(StructField("id",LongType), StructField("",getGeneratorRunReturnType(generator))))
    return new Table[Dataset[Row]](idsTable.getData.map(function, RowEncoder(schema)))
  }

  def execRange( f: Function) : Table[Dataset[Row]] = {
    // get the number of elements to generate
    val numElements = getIntegerLiteral(f.getParameters.get(0).accept(this))
    val ids = spark.range(0,numElements)
    return new Table[Dataset[Row]](ids.withColumn("",ids("id")).toDF())
  }

  def execJoin( f: Function) : Table[Dataset[Row]] = {
    val parameterList = f.getParameters.toList
    val parameterTables = {
      parameterList.map {
        case id: Id => tables(id)
        case variable: Var => variables(variable) match {
          case t: Table[Dataset[Row]] => t
          case _ => throw new ExecutionException("Variables in join must contain tables")
        }
        case _ => throw new ExecutionException("Union only accepts parameters of type id or variable")
      }
    }

    var first = parameterTables.get(0).getData
    parameterTables.drop(1).foreach(x => {
      first = first.join(x.getData,"id")
    })
    new Table[Dataset[Row]](first)
  }

  def execZip( f: Function) : Table[Dataset[Row]] = {
    return execJoin(f)
  }

  def execDump( f: Function) : Table[Dataset[Row]] = {
    val table = getTable(f.getParameters.get(0).accept(this))
    table.getData.coalesce(1).write.format("com.databricks.spark.csv").option("header",true).save(config.outputDir +"/" + (f.getParameters.get(0) match { case id : Id=> id.getValue})+".csv")
    return null;
  }


  /** Helper functions **/
  def getGenerator( expr : ExpressionValue ) : Generator = {
    expr match {
      case g : Generator => return g
      case _ => throw new ExecutionException("ExpressionValue is not of Generator type")
    }
  }

  def getTable( expr : ExpressionValue ) : Table[Dataset[Row]] = {
    expr match {
      case t : Table[Dataset[Row]] => return t
      case _ => throw new ExecutionException("ExpressionValue is not of Generator type")
    }
  }

  def getStringLiteral( expr : ExpressionValue) : String = {
    expr match {
      case l : Literal => l.getLiteral match {
        case s : org.dama.datasynth.schnappi.ast.StringLiteral => return s.getValue
        case _ => throw new ExecutionException("Expression is not a StringLiteral")
      }
      case _ => throw new ExecutionException("Expression is not a Literal")
    }
  }

  def getIntegerLiteral( expr : ExpressionValue) : Int = {
    expr match {
      case l : Literal => l.getLiteral match {
        case n : org.dama.datasynth.schnappi.ast.Number => return n.getValue.toInt
        case _ => throw new ExecutionException("Expression is not an Integer")
      }
      case _ => throw new ExecutionException("Expression is not a Literal")
    }
  }

  def getGeneratorRunReturnType( generator : Generator ) : org.apache.spark.sql.types.DataType = {
    val m = Types.getUntypedMethod(generator.getGenerator,"run")
    Types.DataType.fromString(m.getReturnType.getSimpleName) match {
      case Types.DataType.INTEGER => return IntegerType
      case Types.DataType.STRING => return StringType
      case Types.DataType.LONG => return LongType
      case Types.DataType.DOUBLE => return DoubleType
      case _ => throw new ExecutionException("Unsupported type")
    }
  }

  def getGeneratorRunFunction( generator : Generator , table : Table[Dataset[Row]]) : MapFunction[Row,Row] = {
    val m = new UntypedMethod(generator.getGenerator, "run")
    return new MapFunction[Row,Row]{
      def call(row: Row) : Row = {
        val params : Seq[AnyRef] = row.toSeq.drop(1).map( x => x.asInstanceOf[AnyRef])
        Row(row.get(0),m.invoke(scala.collection.JavaConversions.seqAsJavaList(params)))
      }
    }
  }

  def rowToTuple( row : Row) : Tuple = {
    val tuple = new Tuple
    row.toSeq.drop(1).map( r => tuple.add(r))
    return tuple
  }

  def tupleToRow ( t : Tuple) : Row = {
    return Row.fromSeq(t.asList().toList.toSeq)
  }

  def getBlockGeneratorRunFunction( generator : Generator , table : Table[Dataset[Row]]) : MapPartitionsFunction[Row,Row] = {
    val m = new UntypedMethod(generator.getGenerator, "run")
    return new MapPartitionsFunction[Row,Row]{
      def call(rows: java.util.Iterator[Row]) : java.util.Iterator[Row] = {
        val block = new java.util.LinkedList[Tuple]()
        rows.toList.map( r => block.add(rowToTuple(r)))
        val l = new java.util.LinkedList[Object]
        l.add(block)
        val retBlock = m.invoke(l).asInstanceOf[java.lang.Iterable[Tuple]]
        val retRows = new ListBuffer[Row]
        retBlock.toList.map( x => retRows += tupleToRow(x))
        return retRows.iterator
      }
    }
  }

}