import scala.io.Source

class TextFile(str: String){
    val values = Source.fromFile(str).getLines.toArray
}
