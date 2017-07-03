package org.dama.datasynth

import org.dama.datasynth.executionplan.ExecutionPlan._
import org.dama.datasynth.lang.ReadExecutionPlan
import org.dama.datasynth.schema._
import scala.reflect.runtime.universe._
import scala.collection.mutable

/**
  * Created by joangui on 03/05/2017.
  * Load the Tables corresponding to the PropertyTables
  */
object LoadPropertyTables{


  /**
    * propertyMap is a variable that is reseted for each node Type. It is used to control which property tables
    * can be generated as it depends on their dependencies
    */
  val propertyMap = new mutable.HashMap[String,PropertyTable[_]]()

  /**
    *
    * @param subset set of strings
    * @param set set of string
    * @return subset is subset of set
    */
  private def isSubset(subset:Seq[String],set:scala.collection.Set[String]):Boolean=
  {
    subset.foldLeft(true) ({case (result,element)=> result&set.contains(element) })
  }

  /**
    *
    * @param nodeTypes list of nodeTypes
    * @return the PropertyTables that correspond to properties of the nodes in nodeTypes
    */
  def getPropertyTableNodes(nodeTypes:Seq[NodeType]):Seq[PropertyTable[_]] =
  {
    val propertyTables:Seq[PropertyTable[_]] = nodeTypes.flatMap(nodeType =>
    {
      //We reset the propertyMap for each NodeType
      propertyMap.clear()

      //First we create the PropertyTables which Generator do not depend on any other
      val independentProperties:Seq[Property] = nodeType.properties match {
        case None => Seq()
        case Some(properties) =>properties.filter (property =>
          property.generator.dependencies match
          { case None => true
            case Some(dependency) => dependency.isEmpty }
        )
      }
      val independentPropertyTables:Seq[PropertyTable[_]]=getPropertyTables(nodeType,independentProperties)
      val dependentPropertiesCandidates:Seq[Property] = nodeType.properties match
      {
        case None => Seq()
        case Some(properties) => properties.filter(property=>
          property.generator.dependencies match {
            case None => false
            case Some(dependency) => dependency.nonEmpty })
      }


      //We create the PropertyTables as soon as its dependencies have been created
      val dependentPropertyTables:Seq[PropertyTable[_]] = calculateDependentPropertyTables(nodeType,dependentPropertiesCandidates)
      independentPropertyTables++dependentPropertyTables
    })
    propertyTables
  }

  def calculateDependentPropertyTables(nodeType: NodeType, dependentPropertiesCandidates: Seq[Property] ): Seq[PropertyTable[_]] = {
    if (dependentPropertiesCandidates.isEmpty)
     Seq()
    else {
      val dependentProperties = dependentPropertiesCandidates.filter(dependentProperty=>
        isSubset(dependentProperty.generator.dependencies match {case None => Seq() case Some(dependencies) =>dependencies},propertyMap.keySet))
      val dependentPropertyTables:Seq[PropertyTable[_]] = getPropertyTables(nodeType,dependentProperties)
      dependentPropertyTables ++ calculateDependentPropertyTables(nodeType, (dependentPropertiesCandidates diff dependentProperties))
    }
   // val dependentPropertyTables:Seq[PropertyTable[_]] =dependentPropertyTables ++ getPropertyTables(nodeType,dependentProperties)
    //dependentPropertiesCandidates=dependentPropertiesCandidates diff dependentProperties
  }

  /**
    *
    * @param nodeType given a nodeType.
    * @param properties and the set of its properties which PropertyTable is ready to be created.
    * @return the sequence of ExectionPlanNodes with the PropertyTables
    */
  private def getPropertyTables(nodeType:NodeType,properties:Seq[Property] ):Seq[PropertyTable[_]]=
  {
    properties.map(property => {

      //Disambiguate the property type to call the correct function
      property.dataType.toLowerCase match {
        case "string" =>   getPropertyTable[String](nodeType.name, nodeType.instances, property)
        case "int" =>   getPropertyTable[Int](nodeType.name, nodeType.instances, property)
        case "long" =>   getPropertyTable[Long](nodeType.name, nodeType.instances, property)
        case "double" =>  getPropertyTable[Double](nodeType.name, nodeType.instances, property)
        case "float" =>   getPropertyTable[Float](nodeType.name, nodeType.instances, property)
      } } )
  }

  //Creates the PropertyTable
  /**
    *
    * @param nodeName to which it belongs the propertyTable
    * @param instances number of instances
    * @param property property of type T
    * @return
    */
  def getPropertyTable[T : TypeTag](nodeName:String, instances:Long, property : Property) : PropertyTable[T]=
  {
    val propertyName = property.name;
    val generator = property.generator;

    val propertyTable = PropertyTable[T](nodeName,propertyName,getPropertyGenerator[T](generator),StaticValue[Long](instances))
    propertyMap.put(propertyName,propertyTable)
    propertyTable
  }

  /**
    *
    * @param generator defines the generator's characteristics
    * @return the PropertyGenerator
    */
  def getPropertyGenerator[T : TypeTag](generator:Generator):PropertyGenerator[T]=
  {
    val generatorName = generator.name

    val propertyTables : Seq[PropertyTable[_]] =
      generator.dependencies match
      {
        case None => Seq()
        case Some(dependencies) => dependencies.map(dependency=>
        {propertyMap.get(dependency) match{ case None=> throw  new RuntimeException("Weird Error, this should have never  " +
          "happened: Probably the dependencies chain is ill-formed.")
                                            case Some(dependencyTable) => dependencyTable }})
      }

    val values :  Seq[Value[_]] = ReadExecutionPlan.readInitParameters(generator.initParameters.getOrElse(Seq()))
    PropertyGenerator[T](generatorName,values,propertyTables)
  }
}
