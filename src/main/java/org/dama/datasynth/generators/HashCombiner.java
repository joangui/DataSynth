package org.dama.datasynth.generators;

import java.util.Random;

/**
 * Created by aprat on 5/09/16.
 * This generator is used internally to generate correlated edges.
 * It produces a hash out of a set of values, by combining the object's hashCodes.
 */
public class HashCombiner extends Generator {

    private Random random = null;


    /**
     * Initializes the generator
     */
    public void initialize() {
        random = new Random();
    }

    /**
     * Generates a new hash by combining the given object's hashCodes. The order in which these are passes to the function
     * is important, as it determines the assigned bits.
     * @param args The list of objects to create the hash from
     * @return A hash based on the given objects.
     */
    public Long run(Object... args) {
        int numParameters = args.length;
        if(numParameters == 0) return random.nextLong();
        int bitsPerParameter = 64/numParameters;
        long hashValue = 0;
        int count = 0;
        for(Object o : args) {
            long objectHash = o.hashCode();
            long mask = 0xffffffff;
            mask =  mask << bitsPerParameter;
            mask = ~(mask);
            objectHash = objectHash & mask;
            objectHash <<= bitsPerParameter*count;
            hashValue+=objectHash;
            count++;
        }
        return hashValue;
    }


}
