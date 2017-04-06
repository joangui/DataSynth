package org.dama.datasynth.executionplan

import org.dama.datasynth.executionplan.ExecutionPlan._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by aprat on 31/03/17.
  */
@RunWith(classOf[JUnitRunner])
class ExecutionPlanTest extends FlatSpec with Matchers{

  /** Generator tests **/

  "A PropertyGenerator toString" should "output like [PropertyGenerator,propertyGeneratorName]" in {
    val generator = new PropertyGenerator("path.to.class", List[Parameter](), List[PropertyGenerator]() )
    generator.toString should be ("[PropertyGenerator,path.to.class]")
  }

  "A GraphGenerator toString" should "output like [GraphGenerator,graphGeneratorName]" in {
    val generator = GraphGenerator("path.to.class", Seq())
    generator.toString should be ("[GraphGenerator,path.to.class]")
  }

  /** Parameter tests **/
  "A LongParameter toString" should "output like [LongParameter,value]" in {
    val parameter = LongParameter(10)
    parameter.toString should be ("[LongParameter,10]")
  }

  "A StringParameter toString" should "output like [StringParameter,value]" in {
    val parameter = StringParameter("test")
    parameter.toString should be ("[StringParameter,test]")
  }

  /** Tasks tests **/
  "A CreatePropertyTable toString" should "output like [CreatePropertyTable,tableName]" in {
    val generator = PropertyGenerator("path.to.generator",Seq(),Seq())
    val size = LongParameter(10)
    val task = CreatePropertyTable("tableName",generator,size)
    task.toString should be ("[CreatePropertyTable,tableName]")
  }

  "A CreateEdgeTable toString" should "output like [CreateEdgeTable,tableName]" in {
    val generator = GraphGenerator("path.to.generator",Seq())
    val size = LongParameter(10)
    val task = CreateEdgeTable("tableName",generator,size)
    task.toString should be ("[CreateEdgeTable,tableName]")
  }

  "A TableSize toString" should "output like [TableSize]" in {
    val generator = PropertyGenerator("path.to.generator",Seq(),Seq())
    val size = LongParameter(10)
    val createTable = CreatePropertyTable("tableName",generator,size)
    val task = TableSize(createTable)
    task.toString should be ("[TableSize]")
  }
}
