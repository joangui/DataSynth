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
     // var nodes:Seq[Table]=Seq()

      //If the edge has a correlation we create a temporal edgeTable which names adds the suffix .structure
      val edgeName:String = if(edgeType.correlation.isDefined) s"${edgeType.name}.structure" else edgeType.name
      val source:String = edgeType.source
      val target:String = edgeType.target
      val structure:Structure = edgeType.structure

      val sourceNode:NodeType= nodeTypes.find(_.name==source) match {case None => throw new RuntimeException(s"There is no node type called $source.") case Some(nodeT)=> nodeT}
      val targetNode:NodeType= nodeTypes.find(_.name==target) match {case None => throw new RuntimeException(s"There is no node type called $target.") case Some(nodeT)=> nodeT}

      val structureGenerator:StructureGenerator=createStructureGenerator(structure)

      val size:StaticValue[Long] = StaticValue[Long](
      if(source==target) sourceNode.instances
      else sourceNode.instances+targetNode.instances)

      //We create the corresponding edgeTable
      val edgeTable = EdgeTable(edgeName,structureGenerator,size)
      val edgeTableNodes : Seq[Table] = Seq(edgeTable)

      //If the edge is correlated we create a Match table
      edgeType.correlation match{
        case None =>edgeTableNodes
        case Some (correlation) => {
        val filename: String = correlation.filename
        val sourceProperty: String = correlation.source
        val targetProperty: String = correlation.target
        val sourcePropertyTableName = s"$source.$sourceProperty"
        val targetPropertyTableName = s"$target.$targetProperty"

        val sourcePropertyTable: PropertyTable[_] = propertyTables.find(_.name == sourcePropertyTableName) match {
          case Some(propertyTable) => propertyTable
          case None => throw new RuntimeException(s"There is no PropertyTable for ${sourcePropertyTableName}. Should never happen: check schema validation.") }
        val targetPropertyTable: PropertyTable[_] = propertyTables.find(_.name == targetPropertyTableName) match {
          case Some(propertyTable) => propertyTable
          case None => throw new RuntimeException(s"There is no PropertyTable for ${targetPropertyTableName}. Should never happen: check schema validation.") }

        val matchTable: Table = MatchNode(edgeType.name, sourcePropertyTable, targetPropertyTable, edgeTable, filename)
        val edgeAndMatchTableNodes :Seq[Table]=Seq(edgeTable,matchTable)
          edgeAndMatchTableNodes
      } } })
  }


  def createStructureGenerator(structure: Structure): StructureGenerator = {
    val values :  Seq[Value[_]] = structure.initParameters match {
      case Some(initParameters) =>
        ReadExecutionPlan.readInitParameters(initParameters)
      case None => Seq()
    }
    StructureGenerator(structure.name,values)
  }

}
