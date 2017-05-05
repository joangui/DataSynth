package org.dama.datasynth.lang

import org.dama.datasynth.executionplan.ExecutionPlan._
import org.dama.datasynth.schema._

/**
  * Created by joangui on 03/05/2017.
  * It load the structural tables, which are the EdgeTables and the Match.
  */
object LoadStructuralTables{


  /**
    *
    * @param schema The schema which contains the nodeTypes and the edgeTypes
    * @param propertyTables The property tables that have been generated from the nodeTypes of the schema
    * @return the Tables created by means of the edgeTypes and their correlations
    */
  def getStructuralTables(schema:Schema, propertyTables: Seq[PropertyTable[_]]): scala.Seq[Table] =
  {
    val nodeTypes : Seq[NodeType] = schema.nodeTypes

    val edgeTypes : Seq[EdgeType] = schema.edgeTypes

    //For each edge type
    edgeTypes.flatMap(edgeType => {
      var nodes:Seq[Table]=Seq()

      //If the edge has a correlation we create a temporal edgeTable which names adds the suffix .structure
      val edgeName:String = if(edgeType.correlation.isDefined) s"${edgeType.name}.structure" else edgeType.name
      val source:String = edgeType.source
      val target:String = edgeType.target

      val structure:Structure = edgeType.structure

      val sourceNode:NodeType= nodeTypes.find(_.name==source).get
      val targetNode:NodeType= nodeTypes.find(_.name==target).get

      val structureGenerator:StructureGenerator=createStructureGenerator(structure)

      val size:StaticValue[Long] = StaticValue[Long](
      if(source==target)
        sourceNode.instances
      else
        sourceNode.instances+targetNode.instances)

      //We create the corresponding edgeTable
      val edgeTable = EdgeTable(edgeName,structureGenerator,size)
      nodes=nodes:+edgeTable

      //If the edge is correlated we create a Match table
      if(edgeType.correlation.isDefined)
        {
          val filename:String = edgeType.correlation.get.filename
          val sourceProperty:String = edgeType.correlation.get.source
          val targetProperty:String = edgeType.correlation.get.target

          val sourcePropertyTableName = s"$source.$sourceProperty"
          val targetPropertyTableName = s"$target.$targetProperty"

          val sourcePropertyTable:PropertyTable[_]= propertyTables.find(_.name==sourcePropertyTableName).get
          val targetPropertyTable:PropertyTable[_]= propertyTables.find(_.name==targetPropertyTableName).get

          val matchTable : Table = Match(edgeType.name,sourcePropertyTable,targetPropertyTable,edgeTable,filename)

          nodes=nodes:+matchTable

        }

      nodes
    })
  }


  def createStructureGenerator(structure: Structure): StructureGenerator = {

    val values :  Seq[Value[_]] =
      if( structure.initParameters.isDefined)
      {
        ReadExecutionPlan.readInitParameters(structure.initParameters.get)
      }
      else
      {
        Seq()
      }
    StructureGenerator(structure.name,values)
  }

}
