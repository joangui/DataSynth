package org.dama.datasynth.generators;
import org.dama.datasynth.runtime.Generator;
import org.dama.datasynth.utils.CSVReader;
import org.dama.datasynth.utils.DFSampler;
import org.dama.datasynth.utils.Sampler;

/**
 * Created by quim on 4/19/16.
 */
public class GenFromFileDF extends Generator {
    private Sampler s;
    public GenFromFileDF(){}
    public void initialize(String file,String x, String y, String sep ){
        CSVReader csv = new CSVReader(file,sep);
        this.s = new DFSampler(csv.fetchSubMatrix(Integer.parseInt(x),Integer.parseInt(y)));
    }
    public String run(){
        return s.takeSample();
    }
}
