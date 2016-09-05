package org.dama.datasynth.generators;

import org.dama.datasynth.runtime.Generator;
import umontreal.ssj.probdist.PowerDist;

/**
 * Created by aprat on 5/09/16.
 */
public class ZipfDistribution extends Generator {

    org.apache.commons.math3.distribution.ZipfDistribution zipf = null;

    public void initialize(Double alfa) {
        zipf = new org.apache.commons.math3.distribution.ZipfDistribution(1000, alfa);
    }

    public Long run(Long id) {
        return (long)zipf.sample();

    }
}
