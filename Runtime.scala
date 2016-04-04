import scala.reflect.runtime.{universe => ru}

object Parser {
    def methodSignature[T: ru.TypeTag](x: T, method: String) = {
          try {
              val m = ru.typeOf[T].declaration(ru.newTermName(method)).asMethod
              var l = m.paramss.head.map(x => x.typeSignature)
              //If we don't have parameters, we can always take functions as
              //taking one parameter of type Unit (i.e "an empty parameter")
              l = l match{
                    case _ if l.size == 0 => List(ru.typeOf[scala.Unit])
                    case _ => l
                }
              l:::(m.returnType::Nil)
          } catch {
            //Because all functions have at least one input and one output types
            //(Unit type) if there is an error return the empty list
            case e: ScalaReflectionException => List()
            //case e: Exception => println("General exception error")
        }
    }
    def reflectMethod[T: ru.TypeTag](x: T, method: String) = {
        try {
          //currentMirror.reflectClass(T.asClass)
        } catch {
          case e: Exception => println("General exception error")
        }
    }
}
