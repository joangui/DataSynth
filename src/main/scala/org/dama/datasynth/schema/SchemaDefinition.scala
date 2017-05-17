package org.dama.datasynth.schema

import scala.reflect.runtime.universe._

/**
  * Created by joangui on 13/04/2017.
  * Represents the schema of the graph to be generated. It support NodeTypes and EdgeTypes.
  * Each NodeType has a "name" and a number of "instances" to generate. The NodeType also may
  * include a list of properties. Each property has a "name", a "dataType" and also information
  * about the "generator". The Generator has a name and optionally can define a set of dependencies and
  * some initial parameters in the form List("parameter:DataType").  Each EdgeType has a name define the nodeTypes of
  * the source and the target. Optionally it can define a structure and a correlation. Structure has a name
  * and initial parameters in the form List("parameter:DataType"). Correlation define with which property of node
  * source and target is correlated and the filename gives information about the correlation distribution.
  *
  */

/**
  * It builds a property generator
  * @param name Generator name.
  * @param dependencies is is a set that indicates the depencies of the generator by means of its names.
  * @param initParameters any initial parameter in the format value:type.
  */
  case class Generator(name: String, dependencies: Option[Seq[String]], initParameters: Option[Seq[String]]) {
    val de = dependencies.getOrElse("")
    val in = initParameters.getOrElse("")
    override def toString: String = s"G[$name,$de,$in]"
  }

/**
  * A property.
  * @param name property name.
  * @param dataType data type (String, Int, Long, Float, Double).
  * @param generator the generator used to generate the property.
  */
  case class Property(name: String, dataType: String, generator: Generator) {
    override def toString: String = s"P[$name,$dataType,$generator]"
  }

/**
  * The node type definition.
  * @param name node type name.
  * @param instances number of instances of the node to be generated.
  * @param properties set of properties of the node type.
  */
  case class NodeType(name: String, instances: Long, properties: Option[Seq[Property]]) {
    override def toString: String = {
      val po = properties.getOrElse("")
      s"NT[$name,$instances,$po]"
    }
  }

/**
  * The structural generator.
  * @param name structure generator name
  * @param initParameters any initial parameter in the format value:type.
  */
  case class Structure(name: String, initParameters: Option[Seq[String]]) {
    override def toString: String = {
      val iP = initParameters.getOrElse("")
      s"S[$name,$iP]"
    }
  }

/**
  * A correlation.
  * @param source source node correlated property.
  * @param target  target node correlated property.
  * @param filename filename in which the correlation distribution is specified.
  */
  case class Correlation(source: String, target: String, filename: String) {
    override def toString: String = s"C[$source,$target,$filename]"
  }

/**
  * It defines an edge type
  * @param name the name of the edge type
  * @param source the source node type by its name
  * @param target the target node type by its name
  * @param structure the structure generator
  * @param correlation its correlations
  */
  case class EdgeType(name: String, source: String, target: String, structure: Structure, correlation: Option[Correlation]) {
    override def toString: String = {
      val co = correlation.getOrElse("")
      s"ET[$name,$source,$target,$structure,$co]"
    }
  }


/**
  * The schema to be generated
   * @param nodeTypes a sequence of node types.
  * @param edgeTypes a sequence of edge types.
  */
case class Schema(nodeTypes: List[NodeType], edgeTypes: List[EdgeType]) {
    val sv:SchemaValidator=new SchemaValidator(this)
    sv.validSchema
  }




