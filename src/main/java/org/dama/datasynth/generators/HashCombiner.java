package org.dama.datasynth.generators;

import org.dama.datasynth.runtime.Generator;
import org.dama.datasynth.utils.MurmurHash;

/**
 * Created by aprat on 5/09/16.
 */
public class HashCombiner extends Generator {

    public void initialize() {

    }

    public Long run(Long id, Object... args) {
        int numParameters = args.length;
        int bitsPerParameter = 64/numParameters;
        long hashValue = 0;
        int count = 0;
        for(Object o : args) {
            long objectHash = o.hashCode();
            long mask = 0xffffffff;
            mask <<= bitsPerParameter;
            mask = ~(mask);
            objectHash = objectHash & mask;
            objectHash <<= bitsPerParameter*count;
            hashValue+=objectHash;
            count++;
        }
        return hashValue;
    }


}
