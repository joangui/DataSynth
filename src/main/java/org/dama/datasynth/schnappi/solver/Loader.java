package org.dama.datasynth.schnappi.solver;

import org.apache.commons.io.FileUtils;
import org.dama.datasynth.DataSynth;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

/**
 * Created by quim on 5/24/16.
 */
public final class Loader {

    private static final Logger logger= Logger.getLogger( DataSynth.class.getSimpleName() );

    public static ArrayList<Solver> loadSolvers(String dir){
        ArrayList<Solver> solvers = new ArrayList<>();
        Iterator<File> it = FileUtils.iterateFiles(new File(dir), null, false);
        while(it.hasNext()){
            String fileName = it.next().getPath();
            logger.info("Loading solver: "+fileName);
            solvers.add(Parser.parse(fileName));
        }
        return solvers;
    }
}
