package org.dama.datasynth.generators;

import org.dama.datasynth.utils.CSVReader;
import org.dama.datasynth.utils.MurmurHash;
import org.dama.datasynth.utils.Sampler;
import org.dama.datasynth.utils.UniformDistributionSampler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by aprat on 19/04/16.
 * Generator used to get values correlated with others. Expects a file with two columns.
 * The first contains the prior values while the second contains the correlated ones.
 * Correlated values are returned uniformly distributed. File must be sorted by the first column.
 */
public class UniformCorrelationGenerator extends Generator {

    Map<String,Sampler> samplers;

    /**
     * Initializes the generator
     * @param fileName The file name where the correlated values are stored
     * @param sep The separator that separates the columns
     */
    public void initialize(String fileName, String sep) {
        samplers = new HashMap<String,Sampler>();
        CSVReader reader = new CSVReader(fileName,sep);
        String[] prior = reader.getStringColumn(0);
        String[] posterior = reader.getStringColumn(1);
        ArrayList<String> currentPriorSection = new ArrayList<String>();
        currentPriorSection.add(prior[0]);
        ArrayList<String> currentPosteriorSection = new ArrayList<String>();
        currentPosteriorSection.add(posterior[0]);
        for(int i = 1; i < prior.length; ++i) {
            while(i < prior.length && currentPriorSection.get(0).compareTo(prior[i]) == 0) {
                currentPriorSection.add(prior[i]);
                currentPosteriorSection.add(posterior[i]);
                ++i;
            }
            String[] stringArray = new String[currentPosteriorSection.size()];
            currentPosteriorSection.toArray(stringArray);
            samplers.put(currentPriorSection.get(0),new UniformDistributionSampler(stringArray,12345L));
            if(i < prior.length) {
                currentPriorSection.clear();
                currentPriorSection.add(prior[i]);
                currentPosteriorSection.clear();
                currentPosteriorSection.add(posterior[i]);
            }
        }
        if(currentPriorSection.size() > 0) {
            String[] stringArray = new String[currentPosteriorSection.size()];
            currentPosteriorSection.toArray(stringArray);
            samplers.put(currentPriorSection.get(0),new UniformDistributionSampler(stringArray,12345L));
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
