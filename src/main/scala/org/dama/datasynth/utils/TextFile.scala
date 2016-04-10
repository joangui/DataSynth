/**
  * Created by aprat on 9/04/16.
  */

package org.dama.datasynth.utils {

    import scala.io.Source

    class TextFile(str: String) {

        val values = Source.fromFile(str).getLines.toArray

        def length() : Int = {
          return values.length;
        }
    }

}
