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
 var propertyMap = new mutable.HashMap[String,PropertyTable[_]]()

 /**
   *
   * @param nodeTypes list of nodeTypes
   * @return the PropertyTables that correspond to properties of the nodes in nodeTypes
   */
 def getPropertyTableNodes(nodeTypes:Seq[NodeType]):Seq[PropertyTable[_]] =
 {

   val propertyNodes:Seq[NodeType]=  nodeTypes.filter(_.properties.isDefined);

  val propertyTables:Seq[PropertyTable[_]] = propertyNodes.flatMap(nodeType =>
  {
   //We reset the propertyMap for each NodeType
   propertyMap = new mutable.HashMap[String, PropertyTable[_]]()


   //First we create the PropertyTables which Generator do not depend on any other
   val independentProperties:Seq[Property] = nodeType.properties.get.filter(property=>
   {!property.generator.dependencies.isDefined||(property.generator.dependencies.get.isEmpty)})

   val independentPropertyTables:Seq[PropertyTable[_]]=getPropertyTables(nodeType,independentProperties)


   var dependentPropertiesCandidates:Seq[Property] = nodeType.properties.get.filter(property=>
   {property.generator.dependencies.isDefined && property.generator.dependencies.get.nonEmpty})


   var dependentPropertyTables:Seq[PropertyTable[_]]=Seq()

   //We create the PropertyTables as soon as its dependencies have been created
   while (dependentPropertiesCandidates.nonEmpty)
   {
    val dependentProperties = dependentPropertiesCandidates.filter(dependentProperty=>
     isSubset(dependentProperty.generator.dependencies.get,propertyMap.keySet))

    dependentPropertyTables=dependentPropertyTables ++ getPropertyTables(nodeType,dependentProperties)

    dependentPropertiesCandidates=dependentPropertiesCandidates diff dependentProperties
   }
   independentPropertyTables++dependentPropertyTables
  })

  propertyTables
 }

 /**
   *
   * @param subset set of strings
   * @param set set of string
   * @return subset is subset of set
   */
 private def isSubset(subset:Seq[String],set:scala.collection.Set[String]):Boolean=
 {
  var result = true
  subset.foreach(element=>{
   result&=set.contains(element)
  })
  result
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
   }

  }
  )
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
 def getPropertyGenerator[T](generator:Generator):PropertyGenerator[T]=
 {
  val generatorName = generator.name

  val propertyTables : Seq[PropertyTable[_]] =
   if(generator.dependencies.isDefined) {
     generator.dependencies.get.map(
       dependency=>
       {
         propertyMap.get(dependency).get
       }
     )
   }
   else
     Seq()



   val values :  Seq[Value[_]] =
     if( generator.initParameters.isDefined)
     {
       ReadExecutionPlan.readInitParameters(generator.initParameters.get)
     }
     else
     {
       Seq()
     }
   PropertyGenerator(generatorName,values,propertyTables)
 }
}
