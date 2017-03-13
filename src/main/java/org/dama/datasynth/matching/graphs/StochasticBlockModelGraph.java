package org.dama.datasynth.matching.graphs;

import org.dama.datasynth.matching.StochasticBlockModel;
import org.dama.datasynth.matching.graphs.types.Graph;

import java.util.Random;

import static org.apache.commons.lang.math.RandomUtils.nextDouble;

/**
 * Created by aprat on 9/03/17.
 */
public class StochasticBlockModelGraph extends Graph {

    public StochasticBlockModelGraph(StochasticBlockModel blockModel) {
        Random random = new Random(0L);
        long indexes[] = new long[blockModel.getNumBlocks()];
        indexes[0] = 0;
        for(int i = 1; i < blockModel.getNumBlocks(); ++i) {
            indexes[i] = indexes[i-1] + blockModel.getSizes()[i-1];
        }
        for(int k = 0; k < blockModel.getNumBlocks(); ++k) {
            for(int i = 0; i < blockModel.getSizes()[k]; ++i) {
                for (int j = i + 1; j < blockModel.getSizes()[k]; ++j) {
                    double prob = random.nextDouble();
                    if(prob < blockModel.getProbabilities()[k][k]){
                        addEdge(i + indexes[k], j + indexes[k]);
                        addEdge(j + indexes[k], i + indexes[k]);
                    }
                }
            }
        }
        for(int k = 0; k < blockModel.getNumBlocks(); ++k) {
            for (int t = k + 1; t < blockModel.getNumBlocks(); ++t) {
                for(int i = 0; i < blockModel.getSizes()[k]; ++i) {
                    for (int j = i + 1; j < blockModel.getSizes()[t]; ++j) {
                        double prob = random.nextDouble();
                        if (prob < blockModel.getProbabilities()[k][t]) {
                            addEdge(i+indexes[k],j+indexes[t]);
                            addEdge(j+indexes[t],i+indexes[k]);
                        }
                    }
                }
            }
        }
    }
}
