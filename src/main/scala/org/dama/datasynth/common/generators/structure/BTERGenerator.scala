package org.dama.datasynth.common.generators.structure
import ldbc.snb.bteronh.hadoop.HadoopBTERGenerator
import org.apache.hadoop.conf.Configuration

/**
  * Created by aprat on 20/04/17.
  *
  * Structure generator that implements the BTER generator, using the hadoop implementation
  * found in https://github.com/DAMA-UPC/BTERonH
  */
class BTERGenerator extends StructureGenerator {

  var degreesFileName : String = "file://./src/main/resources/degreeSequences/dblp"
  var ccsFileName : String = "file://./src/main/resources/CCs/dblp"
  var conf : Configuration = null

  override def initialize(parameters: Any*): Unit = {
    parameters(0) match { case s : String => degreesFileName = s }
    parameters(1) match { case s : String => ccsFileName = s }
  }

  override def run(num: Long, hdfsConf: Configuration, path: String): Unit = {

    conf = new Configuration(hdfsConf)
    conf.setInt("ldbc.snb.bteronh.generator.numThreads", 4)
    conf.setLong("ldbc.snb.bteronh.generator.numNodes", num)
    conf.setInt("ldbc.snb.bteronh.generator.seed", 12323540)
    conf.set("ldbc.snb.bteronh.serializer.workspace", "hdfs:///tmp")
    conf.set("ldbc.snb.bteronh.serializer.outputFileName", path)
    conf.set("ldbc.snb.bteronh.generator.degreeSequence", degreesFileName)
    conf.set("ldbc.snb.bteronh.generator.ccPerDegree", ccsFileName)

    val generator = new HadoopBTERGenerator(conf)
    generator.run()
  }
}
