package org.dama.datasynth.generators;

import org.dama.datasynth.utils.CDFSampler;
import org.dama.datasynth.utils.CSVReader;
import org.dama.datasynth.utils.MurmurHash;
import org.dama.datasynth.utils.Sampler;


/**
 * Created by quim on 4/19/16.
 * Cummulative distribution function generator. This generator returns values of a dictionary
 * containing the cumulative distribution function
 */
public class CDFGenerator extends Generator {
        private Sampler s;
        public CDFGenerator(){}

    /**
     * Initializes the generator
     * @param file The file name of the dictionary
     * @param x The column containing the dictionary values
     * @param y The column containing the probability values
     * @param sep The sparator used to separate the values
     */
    public void initialize(String file, Long x, Long y, String sep ){
        CSVReader csv = new CSVReader(file, sep);
        this.s = new CDFSampler(csv.fetchSubMatrix(x.intValue(), y.intValue()), 12345L);
    }


    /**
     * Returns a value of the dictionary based on the given cummulative distribution function
     * @param id Unused
     * @return The dictionary value.
     */
    public String run(Long id){
        return s.takeSample(MurmurHash.hash64(id.toString()));
        }
}
