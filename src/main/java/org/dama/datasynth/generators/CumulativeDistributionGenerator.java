package org.dama.datasynth.generators;

import org.dama.datasynth.utils.CumulativeDistributionSampler;
import org.dama.datasynth.utils.CSVReader;
import org.dama.datasynth.utils.Sampler;


/**
 * Created by quim on 4/19/16.
 * Cummulative distribution function generator. This generator returns values of a dictionary
 * containing the cumulative distribution function
 */
public class CumulativeDistributionGenerator extends Generator {
        private Sampler s;

    /**
     * Initializes the generator
     * @param file The file name of the dictionary
     * @param x The column containing the dictionary values
     * @param y The column containing the probability values
     * @param sep The sparator used to separate the values
     */
    public void initialize(String file, Long x, Long y, String sep ){
        CSVReader csv = new CSVReader(file, sep);
        this.s = new CumulativeDistributionSampler(csv.getStringColumn(x.intValue()),csv.getDoubleColumn(y.intValue()), 12345L);
    }


    /**
     * Returns a value of the dictionary based on the given cummulative distribution function
     * @return The dictionary value.
     */
    public String run(){
        return s.takeSample();
    }
}
