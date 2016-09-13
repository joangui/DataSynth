package org.dama.datasynth.generators;
import org.dama.datasynth.utils.CSVReader;
import org.dama.datasynth.utils.DFSampler;
import org.dama.datasynth.utils.MurmurHash;
import org.dama.datasynth.utils.Sampler;

/**
 * Created by quim on 4/19/16.
 * This generator samples values from a dictionary containing an associated probability distribution function
 */
public class DFGenerator extends Generator {

    private Sampler s;
    public DFGenerator(){}

    /**
     * Initializes the generator
     * @param file The file name of the dictionary
     * @param x The column index containing the values to sample from
     * @param y The column index containing the probability distribution
     * @param sep The separator of the columns
     */
    public void initialize(String file, Long x, Long y, String sep ){
        CSVReader csv = new CSVReader(file,sep);
        this.s = new DFSampler(csv.fetchSubMatrix(x.intValue(),y.intValue()));
    }

    /**
     * Returns a value from the dictionary distributed according to the specified probability distribution function
     * @param id Unused
     * @return A value of the dictionary distrubted according to the specified probability distribution function
     */
    public String run(Long id){
        return s.takeSample(MurmurHash.hash64(id.toString()));
    }
}
