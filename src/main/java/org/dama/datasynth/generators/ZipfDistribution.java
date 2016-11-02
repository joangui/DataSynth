package org.dama.datasynth.generators;

/**
 * Created by aprat on 5/09/16.
 * Generates values distributed using a Zipf distribution
 */
public class ZipfDistribution extends Generator {

    org.apache.commons.math3.distribution.ZipfDistribution zipf = null;

    /**
     * Initializes the generator
     * @param alfa The parameter alfa of the Zipf distribution.
     */
    public void initialize(Double alfa) {
        zipf = new org.apache.commons.math3.distribution.ZipfDistribution(1000, alfa);
    }

    /**
     * Returns a value with the Zipf probability distribution
     * @return A value with the Zipf probability distribution
     */
    public Long run(Long id) {
        return (long)zipf.sample();

    }
}
