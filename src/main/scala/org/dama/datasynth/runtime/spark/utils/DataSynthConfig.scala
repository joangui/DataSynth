package org.dama.datasynth.runtime.spark.utils

/**
  * Created by aprat on 3/05/17.
  *
  * Class used to store the configuration of DataSynth
  *
  */

object DataSynthConfig {
  def apply() : DataSynthConfig = {
    new DataSynthConfig()
  }

  def apply( args : List[String] ) : DataSynthConfig = {
    nextOption( new DataSynthConfig(), args)
  }

  def nextOption(currentConfig : DataSynthConfig, list: List[String]) : DataSynthConfig = {
    def isSwitch(s : String) = (s(0) == '-')
    list match {
      case "--output-dir" :: value :: tail if !isSwitch(value) => {
        val config = new DataSynthConfig(value,currentConfig.schemaFile)
        nextOption(config, tail)
      }
      case "--schema-file" :: value :: tail if !isSwitch(value) => {
        val config = new DataSynthConfig(currentConfig.outputDir,value)
        nextOption(config, tail)
      }
      case option :: tail => {
        throw new Exception(s"Unknown option $option")
      }
      case Nil => currentConfig
    }
  }
}

class DataSynthConfig (
  val outputDir : String = "./datasynth",
  val schemaFile : String = "./schema.json" )
  {

  /**
    * Sets the outputDir
    * @param value The value of the output dir
    * @return this
    */
  def outputDir( value : String ) : DataSynthConfig = {
    new DataSynthConfig(value,schemaFile)
  }

  /**
    * Sets the schema file path
    * @param value The value of the schema file path
    * @return this
    */
  def schemaFile( value : String ) : DataSynthConfig = {
    new DataSynthConfig(outputDir,value)
  }
}
