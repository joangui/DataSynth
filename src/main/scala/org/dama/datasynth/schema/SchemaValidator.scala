package org.dama.datasynth.schema

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

    nodeTypes.foreach(nodeType => {
      nodeType.properties.foreach(properties => {
        properties.foreach(property => {
          try {
            findCycles(Seq(property), properties)
          }
          catch {
            case r: RuntimeException =>
              throw new RuntimeException(s"Exist a cyclic dependency in node '${nodeType.name}' for its property '${r.getMessage}'.");
          }
        })
      })
    })
  }


  private def findCycles(stack: Seq[Property], properties: Seq[Property]): Unit = {
    stack.lastOption.foreach(property => {
      property.generator.dependencies.foreach(dependencies => {
        dependencies.foreach(dependency => {
          properties.find(_.name == dependency).foreach(neighborProperty => {
            if (stack.contains(neighborProperty))
              throw new RuntimeException(s"${dependency}")
            else
              findCycles(stack :+ neighborProperty, properties)
          })
        })
      })
    })
  }

  /**
    * Checks whether each nodeType and edgeType has a unique name.
    */
  private def uniquenessTypes: Unit = {
    /*val repeatedNodes:Set[String] = checkUniqueness(nodeTypes.map(nodeType=>nodeType.name))
    if(!repeatedNodes.isEmpty) {
      throw new RuntimeException(s"The node(s) '${repeatedNodes.mkString(", ")}' already exist(s).")
    }*/
    val nodeNames = nodeTypes.map(nodeType => nodeType.name)
    if (nodeNames.distinct.size != nodeNames.size)
      throw new RuntimeException("There are repeated node type names.")

    val edgeNames = edgeTypes.map(edgeType => edgeType.name)
    if (edgeNames.distinct.size != edgeNames.size)
      throw new RuntimeException("There are repeated edge type names.")
  }

  /**
    * Checks whether each nodeType has a set of properties each of which has a unique name.
    */
  private def uniquenessNodeProperties: Unit = {
    nodeTypes.foreach(nodeType => {
      nodeType.properties.foreach(properties => {
        val propertiesNames = properties.map(property => property.name)
        if (propertiesNames.distinct.size != propertiesNames.size)
          throw new RuntimeException(s"There are repeated property(ies) name(s) for the node type '${nodeType.name}'.")
      })
    })
  }

  /**
    * Validates that the dependencies of the properties generators match with other properties
    *
    */
  private def validNodeTypesDependencies: Unit = {
    nodeTypes.foreach(nodeType => {
      nodeType.properties.foreach(properties => {
        val propertyNames = properties.map(property => property.name)
        properties.foreach(property => {
          property.generator.dependencies.foreach(dependencies => {
            val valid: Boolean = dependencies.foldLeft(true)({ case (result, dependency) => result & propertyNames.contains(dependency) })
            if (!valid)
              throw new RuntimeException(s"For the node '${nodeType.name}' the dependencies on the property '${property.name}' cannot be established some does not exist.");
          })
        })
      })
    })
  }

  /**
    * Validates that the edges are definited correctly i.e. exist source and target nodeTypes
    */
  private def validateEdges: Unit = {
    edgeTypes.foreach {
      edgeType =>
        if (nodeTypes.filter(_.name == edgeType.source).isEmpty)
          throw new RuntimeException(s"The edge '${edgeType.name}' is not well-defined because the source node type, '${edgeType.source}' does not exist.")
        if (nodeTypes.filter(_.name == edgeType.target).isEmpty)
          throw new RuntimeException(s"The edge '${edgeType.name}' is not well-defined because the target node type, '${edgeType.target}' does not exist.")

    }
  }

  /**
    * Validates that the correlations of the edges match with a property of source/target node
    *
    */
  private def validEdgeTypeCorrelations: Unit = {
    edgeTypes.foreach {
      edgeType =>
        val sourceNode: NodeType = nodeTypes.filter(_.name == edgeType.source)(0)
        val targetNode: NodeType = nodeTypes.filter(_.name == edgeType.target)(0)

        edgeType.correlation match {
          case None =>
          case Some(correlation) =>
            if (!validateCorrelations(sourceNode, correlation.source))
              throw new RuntimeException(s"The correlations for '${edgeType.name}' are not well-defined because some source property does not exist.")
            if (!validateCorrelations(sourceNode, correlation.target))
              throw new RuntimeException(s"The correlations for '${edgeType.name}' are not well-defined because some target property does not exist.")
        }
    }
  }

  /**
    *
    * @param node        Its is a node
    * @param correlation A correlations
    */
  private def validateCorrelations(node: NodeType, correlation: String): Boolean = {

    val valid: Boolean = node.properties.foldLeft(true)({ case (result, properties) => {
      val propertyNames = properties.map(property => property.name)
      result & propertyNames.contains(correlation)
    }
    })
    valid
  }
}
