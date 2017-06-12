package org.dama.datasynth

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

  /**
    * Parses the next option from the option list
    * @param currentConfig The current DataSynth config
    * @param list The list of remaining options to parse
    * @return The new DataSynth config
    */
  def nextOption(currentConfig : DataSynthConfig, list: List[String]) : DataSynthConfig = {
    def isSwitch(s : String) = (s(0) == '-')
    list match {
      case "--output-dir" :: value :: tail if !isSwitch(value) => {
        val config = new DataSynthConfig(value,currentConfig.schemaFile,currentConfig.driverWorkspaceDir)
        nextOption(config, tail)
      }
      case "--schema-file" :: value :: tail if !isSwitch(value) => {
        val config = new DataSynthConfig(currentConfig.outputDir,value,currentConfig.driverWorkspaceDir)
        nextOption(config, tail)
      }
      case "--driver-workspace-dir" :: value :: tail if !isSwitch(value) => {
        //val config = new DataSynthConfig(currentConfig.outputDir,currentConfig.schemaFile,value)
        val config = new DataSynthConfig(currentConfig.outputDir,currentConfig.schemaFile,currentConfig.driverWorkspaceDir)
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
                        val schemaFile : String = "./schema.json",
                        val driverWorkspaceDir : String = "/tmp")
{

  /**
    * Sets the outputDir
    * @param value The value of the output dir
    * @return this
    */
  def outputDir( value : String ) : DataSynthConfig = {
    new DataSynthConfig(value,schemaFile,driverWorkspaceDir)
  }

  /**
    * Sets the schema file path
    * @param value The value of the schema file path
    * @return this
    */
  def schemaFile( value : String ) : DataSynthConfig = {
    new DataSynthConfig(outputDir,value,driverWorkspaceDir)
  }

  /**
    * Sets the driver workspace dir
    * @param value The value of the driver workspace dir
    * @return this
    */
  def driverWorkspaceDir( value : String ) : DataSynthConfig = {
    new DataSynthConfig(outputDir,schemaFile,driverWorkspaceDir)
  }
}
