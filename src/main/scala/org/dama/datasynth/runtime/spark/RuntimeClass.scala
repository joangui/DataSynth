package org.dama.datasynth.runtime.spark

import scala.collection.mutable

/**
  * Created by joangui on 05/07/2017.
  */
class RuntimeClass(val propertyTableName:String, val className:String, val code:String)

class RuntimeClasses()
{
  val runtimeClasses =  scala.collection.mutable.Map[String,(String,String)]()

  def add(newClasses: Map[String,(String,String)]): Unit = {
    newClasses.foreach({case (entry)=>runtimeClasses+entry})
  }

  def  classNameToClassCode  : Map[String,String]= runtimeClasses.foldLeft(Map[String,String]())({case (classesCode,(_,(filename,filecode)))=>classesCode + (filename->filecode)})
  def propertyTableNameToClassName : mutable.Map[String, String]= runtimeClasses.map( { case (propertyTable,(className,_)) => (propertyTable -> className) })

  def addClass(codeClass:RuntimeClass)={runtimeClasses += codeClass.propertyTableName->(codeClass.className,codeClass.code)}
  def addClass(propertyTableName:String,className:String,codeClass:String):Unit={runtimeClasses+=propertyTableName->(className,codeClass)}

  def +(codeClass:RuntimeClass):RuntimeClasses={
    addClass(codeClass)
    this
  }

  def ++(codeClasses:RuntimeClasses):RuntimeClasses={
    runtimeClasses++=codeClasses.runtimeClasses
    this}

}
