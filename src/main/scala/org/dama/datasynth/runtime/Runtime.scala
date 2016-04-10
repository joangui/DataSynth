

package org.dama.datasynth.runtime {

  import scala.reflect.runtime.{universe => ru}
  import org.apache.spark._
  import org.apache.spark.rdd.RDD

  object Runtime {
    def methodSignature[T: ru.TypeTag](x: T, method: String) = {
      try {
        val m = ru.typeOf[T].declaration(ru.newTermName(method)).asMethod
        var l = m.paramss.head.map(x => x.typeSignature)
        //If we don't have parameters, we can always take functions as
        //taking one parameter of type Unit (i.e "an empty parameter")
        l = l match {
          case _ if l.size == 0 => List(ru.typeOf[scala.Unit])
          case _ => l
        }
        l ::: (m.returnType :: Nil)
      } catch {
        //Because all functions have at least one input and one output types
        //(Unit type) if there is an error return the empty list
        case e: ScalaReflectionException => List()
        //case e: Exception => println("General exception error")
      }
    }

    def reflectMethod(x: Any, method: String) = {
      //try {
      val m = ru.runtimeMirror(getClass.getClassLoader).reflect(x)
      val f = m.symbol.typeSignature.member(ru.newTermName(method))
      //m.reflectMethod(f.asMethod)(n)
      m.reflectMethod(f.asMethod)
      /*} catch {
          case e: Exception => println("General exception error")
      }*/
    }

    def cast[A](a: Any, tt: ru.TypeTag[A]): A = a.asInstanceOf[A]

    def applyMap[A: ru.TypeTag, B: ru.TypeTag](d: RDD[_], x: Any, method: String): RDD[_] = {
      d.map(x => reflectMethod(x, method)(x))
    }
  }
}
