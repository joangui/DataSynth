package org.dama.datasynth

import java.io.File
import java.nio.file.{Files, Path, Paths}

import org.apache.commons.io.FileUtils
import org.apache.spark.sql.SparkSession
import org.junit.runner.RunWith
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}
import org.scalatest.junit.JUnitRunner

import scala.util.Try

/**
  * Created by aprat on 17/07/17.
  */
@RunWith(classOf[JUnitRunner])
class DataSynthTest extends FlatSpec with Matchers with BeforeAndAfterAll {


  " The test schema at /src/test/resources/test.json should work " should " work " in {

    SparkSession.builder().master("local[*]").getOrCreate()

    val testFolder = new File("./test")
    val dataFolder = new File("./test/data")
    val workspaceFolder = new File("./test/workspace")
    testFolder.mkdir()
    dataFolder.mkdir()
    workspaceFolder.mkdir()
    val result = Try(DataSynth.main(List("--output-dir", dataFolder.getAbsolutePath,
      "--driver-workspace-dir", workspaceFolder.getAbsolutePath,
      "--schema-file", "src/test/resources/test.json").toArray))
    result.isSuccess should be(true)
  }
}
