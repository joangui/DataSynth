package org.dama.datasynth.schema

import scala.collection.immutable

/**
  * Created by joangui on 04/05/2017. This class is used to validate a give schema.
  * It checks the uniqueness of types and properties per type, the node types dependencies,
  * that edges connect existing node types, correlations with existent properties and no cycle dependencies.
  */
class SchemaValidator(schema : Schema) {
  val nodeTypes: List[NodeType] = schema.nodeTypes;
  val edgeTypes: List[EdgeType] = schema.edgeTypes;

  /**
    * Checks whether it is a valid schema or not.
    */
  def validSchema: Unit = {
    uniquenessTypes
    uniquenessNodeProperties
    validNodeTypesDependencies
    validateEdges
    validEdgeTypeCorrelations
    validateCycleDependencies
  }


  /**
    * Validates that it does exist cycles among the dependencies of the properties
    */
  private def validateCycleDependencies: Unit = {
    nodeTypes.filter(_.properties.isDefined).foreach(nodeType => {
      try {
       nodeType.properties.get.filter(_.generator.dependencies.isDefined).foreach(property=>{
          findCycles(Seq(property),nodeType.properties.get)
        })
      }
      catch
      {
        case r: RuntimeException =>
          throw new RuntimeException(s"Exist a cyclic dependency in node '${nodeType.name}' for its property '${r.getMessage}'.");
      }
    })
  }


  private def findCycles(stack: Seq[Property],properties:Seq[Property]): Unit =
  {
    val p:Property = stack.last
    if(p.generator.dependencies.isDefined)
      p.generator.dependencies.get.foreach(dependency=>{
        val neighborProperty = properties.find(_.name==dependency).get
        if(stack.contains(neighborProperty))
        {
          throw new RuntimeException(s"${dependency}")
        }
        else
        {
          findCycles(stack:+neighborProperty,properties)
        }
      })
  }

  private def checkUniqueness(typeNames:Seq[String]):Set[String]={
    var names: Seq[String] = Seq()
    var repeatedNames: Set[String] = Set()
    typeNames.foreach{name=>
      if(names.contains(name)) repeatedNames+=name
      names=names:+name
    }
    repeatedNames
  }

  /**
    * Checks whether each nodeType and edgeType has a unique name.
    */
  private def uniquenessTypes: Unit = {
    val repeatedNodes:Set[String] = checkUniqueness(nodeTypes.map(nodeType=>nodeType.name))
    if(!repeatedNodes.isEmpty) {
      throw new RuntimeException(s"The node(s) '${repeatedNodes.mkString(", ")}' already exist(s).")
    }

    val repeatedEdges:Set[String] = checkUniqueness(edgeTypes.map(edgeType=>edgeType.name))
    if(!repeatedEdges.isEmpty) {
      throw new RuntimeException(s"The edge(s) '${repeatedEdges.mkString(", ")}' already exist(s).")
    }
  }

  /**
    * Checks whether each nodeType has a set of properties each of which has a unique name.
    */
  private def uniquenessNodeProperties : Unit =
  {
    nodeTypes.filter(_.properties.isDefined).foreach{nodeType=>
        val repeatedNames:Set[String] = checkUniqueness(nodeType.properties.get.map(property=>property.name))
        if(!repeatedNames.isEmpty) {
          throw new RuntimeException(s"The property(ies) '${repeatedNames.mkString(", ")}' is defined multiple times for the node '${nodeType.name}'.");
        }
    }
  }

  /**
    * Validates that the dependencies of the properties generators match with other properties
    *
    */
  private def validNodeTypesDependencies: Unit = {

    var valid: Boolean = true
    nodeTypes.foreach { nodeType =>

      if (nodeType.properties.isDefined) {

        val names=nodeType.properties.get.map(property=>property.name)

        nodeType.properties.get.foreach { property =>
          if (property.generator.dependencies.isDefined) {
            val dependencies: Seq[String] = property.generator.dependencies.get

            dependencies.foreach {
              dependency =>{
                valid &= names.contains(dependency)
                if(!valid)
                  throw new RuntimeException(s"For the node '${nodeType.name}' the dependency on the property '${property.name}', '${dependency}', cannot be established because it does not exist.");
            }}
          }
        }
      }
    }
  }

  /**
    * Validates that the edges are definited correctly i.e. exist source and target nodeTypes
    */
  private def validateEdges: Unit = {
    edgeTypes.foreach {
      edgeType =>
        if(nodeTypes.filter(_.name == edgeType.source).isEmpty )
          throw new RuntimeException(s"The edge '${edgeType.name}' is not well-defined because the source node type, '${edgeType.source}' does not exist." )
        if(nodeTypes.filter(_.name == edgeType.target).isEmpty )
          throw new RuntimeException(s"The edge '${edgeType.name}' is not well-defined because the target node type, '${edgeType.target}' does not exist." )

    }
  }

  /**
    * Validates that the correlations of the edges match with a property of source/target node
    *
    */
  private def validEdgeTypeCorrelations: Unit = {
    edgeTypes.foreach {
      edgeType =>
        //if(!validateEdges) return false
        val sourceNode: NodeType = nodeTypes.filter(_.name == edgeType.source)(0)
        val targetNode: NodeType =nodeTypes.filter(_.name == edgeType.target)(0)

        if (edgeType.correlation.isDefined) {
          if(!validateCorrelations(sourceNode, edgeType.correlation.get.source) )
            throw new RuntimeException(s"The correlations for '${edgeType.name}' are not well-defined because some source property does not exist." )
          if(!validateCorrelations(sourceNode, edgeType.correlation.get.target) )
            throw new RuntimeException(s"The correlations for '${edgeType.name}' are not well-defined because some target property does not exist." )
        }
    }
  }

  /**
    *
    * @param node Its is a node
    * @param correlation A correlations
    */
  private def validateCorrelations(node: NodeType, correlation:String): Boolean = {
    var valid: Boolean = true
    //We obtain all the property names of the node nodeType
    valid = if (node.properties.isDefined) {

      val names = node.properties.get.map(property=>property.name)

      names.contains(correlation)

    }
    else
      false
    if (correlation.isEmpty) valid= true

    valid
  }

}
