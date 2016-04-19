package org.dama.datasynth.generators;

import org.dama.datasynth.runtime.Generator;
import org.dama.datasynth.utils.CSVReader;
import org.dama.datasynth.utils.Sampler;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by aprat on 19/04/16.
 */
public class CorrellationGenerator extends Generator {

    Map<String,Sampler> samplers;

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

    public String run( String country ) {
        return samplers.get(country).takeSample();
    }

}
