package org.dama.datasynth.test.matching;

import org.dama.datasynth.common.Types;
import org.dama.datasynth.test.graphreader.types.Graph;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by aprat on 2/03/17.
 */
public class Matching {
    public static <XType extends Comparable<XType>, YType extends Comparable<YType>> void  run(Graph graph,
                                           ArrayList<XType> tableX,
                                           ArrayList<YType> tableY,
                                           JointDistribution<XType,YType> distribution ) {

        EdgeTypePool<String,String> edgeTypePool = null;

    }
}
