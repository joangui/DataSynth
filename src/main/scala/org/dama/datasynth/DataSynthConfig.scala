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
      case "--output-dir" :: outputdir :: tail if !isSwitch(outputdir) => {
        val config = currentConfig.setOutputDir(outputdir)
        nextOption(config, tail)
      }
      case "--schema-file" :: schema :: tail if !isSwitch(schema) => {
        val config = currentConfig.schemaFile(schema)
        nextOption(config, tail)
      }
      case "--driver-workspace-dir" :: workspace :: tail if !isSwitch(workspace) => {
        val config = currentConfig.driverWorkspaceDir(workspace)
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
    * @param newOutputDir The value of the output dir
    * @return this
    */
  def setOutputDir(newOutputDir : String ) : DataSynthConfig = {
    new DataSynthConfig(newOutputDir,schemaFile,driverWorkspaceDir)
  }

  /**
    * Sets the schema file path
    * @param newSchemaFile The value of the schema file path
    * @return this
    */
  def schemaFile(newSchemaFile : String ) : DataSynthConfig = {
    new DataSynthConfig(outputDir,newSchemaFile,driverWorkspaceDir)
  }

  /**
    * Sets the driver workspace dir
    * @param newWorkspace The value of the driver workspace dir
    * @return this
    */
  def driverWorkspaceDir(newWorkspace : String ) : DataSynthConfig = {
    new DataSynthConfig(outputDir,schemaFile,newWorkspace)
  }
}
