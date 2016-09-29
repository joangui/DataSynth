package org.dama.datasynth;

/**
 * Created by aprat on 19/04/16.
 */


import com.beust.jcommander.Parameter;

import java.io.Serializable;

public class DataSynthConfig implements Serializable {

    @Parameter(names={"--outputDir","-o"}, description = "Output dir")
    public String outputDir = "./";

    @Parameter(names={"--query","-q"}, description = "Query file name", required=true)
    public String queryFile;

    @Parameter(names={"--frontend","-fe"}, description = "Execute only the frontend")
    public boolean frontend = false;

    @Parameter(names={"--debug","-d"}, description = "Execute in debug mode")
    public boolean debug = false;

    @Parameter(names = {"--help","-h"}, help = true)
    public boolean help = false;
}
