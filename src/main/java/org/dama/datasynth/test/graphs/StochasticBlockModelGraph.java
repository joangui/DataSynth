package org.dama.datasynth.test.graphs;

import org.dama.datasynth.test.graphs.types.Graph;

import java.util.Random;

/**
 * Created by aprat on 9/03/17.
 */
public class StochasticBlockModelGraph extends Graph {

    public StochasticBlockModelGraph( long blockSize, double pin, double pout) {
        Random random = new Random(0L);
        for(int i = 0; i < blockSize; i+=1) {
            for(int j = i+1 ; j < blockSize; j+=1) {
                double prob = random.nextDouble();
                if( prob < pin) {
                    addEdge(i, j);
                    addEdge(j, i);
                }
                prob = random.nextDouble();
                if( prob < pin) {
                    addEdge(i+blockSize, j+blockSize);
                    addEdge(j+blockSize, i+blockSize);
                }
            }
        }
        for(int i = 0; i < blockSize; i+=1) {
            for(int j = 0; j < blockSize; j+=1) {
                double prob = random.nextDouble();
                if(prob < pout) {
                    addEdge(i,j+blockSize);
                    addEdge(j+blockSize,i);
                }
            }
        }
    }
}
