package org.dama.datasynth.generators;

import org.dama.datasynth.utils.CSVReader;
import org.dama.datasynth.utils.MurmurHash;
import org.dama.datasynth.utils.Sampler;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by aprat on 19/04/16.
 * Generator used to get values correlated with others. Expects a file with two columns.
 * The first contains the prior values while the second contains the correlated ones.
 * Correlated values are returned uniformly distributed. File must be sorted by the first column.
 */
public class CorrellationGenerator extends Generator {

    Map<String,Sampler> samplers;

    /**
     * Initializes the generator
     * @param fileName The file name where the correlated values are stored
     * @param sep The separator that separates the columns
     */
    public void initialize(String fileName, String sep) {
        samplers = new HashMap<String,Sampler>();
        CSVReader reader = new CSVReader(fileName,sep);
        String csv = reader.toString(sep,";");
        String [] lines = csv.split(";");
        String currentString = lines[0].split(sep)[0];
        StringBuilder sb = new StringBuilder();
        String [] fields = lines[0].split(sep);
        sb.append(","+fields[1]+" "+fields[1]);
        for(int i = 1; i < lines.length; ++i) {
            while(i < lines.length && currentString.compareTo(lines[i].split(sep)[0]) == 0) {
                fields = lines[i].split(sep);
                sb.append(","+fields[1]+" "+fields[1]);
                ++i;
            }
            samplers.put(currentString,new Sampler(sb.toString()));
            if(i < lines.length) {
                currentString = lines[i].split(sep)[0];
                sb = new StringBuilder();
                fields = lines[i].split(sep);
                sb.append(","+fields[1]+" "+fields[1]);
            }
        }
    }

    /**
     * Given a value, returns the correlated one
     * @param prior The prior value.
     * @return A correlated value uniformly distributed
     */
    public String run(String prior) {
        return samplers.get(prior).takeSample();
    }

}
