package org.dama.datasynth.matching;

import java.util.Map;

/**
 * Created by aprat on 10/03/17.
 */
public class StochasticBlockModelMatching implements Matching {

    @Override
    public <XType extends Comparable<XType>> Map<Long, Long> run(Table<Long, Long> graph, Table<Long, XType> attributes, JointDistribution<XType, XType> distribution) {
        return null;
    }
}
