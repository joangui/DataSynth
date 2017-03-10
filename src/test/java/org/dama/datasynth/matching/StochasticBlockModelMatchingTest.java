package org.dama.datasynth.matching;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

import static org.junit.Assert.assertTrue;

/**
 * Created by aprat on 10/03/17.
 */
public class StochasticBlockModelMatchingTest {

    @Test
    public void mappingSizeTest() {

        Table<Long,String> table = new Table<>();
        table.add(new Tuple<>(0L,"A"));
        table.add(new Tuple<>(1L,"A"));
        table.add(new Tuple<>(2L,"A"));
        table.add(new Tuple<>(3L,"A"));
        table.add(new Tuple<>(4L,"A"));
        table.add(new Tuple<>(5L,"B"));
        table.add(new Tuple<>(6L,"B"));
        table.add(new Tuple<>(7L,"B"));
        table.add(new Tuple<>(8L,"B"));
        table.add(new Tuple<>(9L,"B"));
        table.add(new Tuple<>(10L,"C"));
        table.add(new Tuple<>(11L,"C"));
        table.add(new Tuple<>(12L,"C"));
        table.add(new Tuple<>(13L,"C"));
        table.add(new Tuple<>(14L,"C"));
        table.add(new Tuple<>(15L,"D"));
        table.add(new Tuple<>(16L,"D"));
        table.add(new Tuple<>(17L,"D"));
        table.add(new Tuple<>(18L,"D"));
        table.add(new Tuple<>(19L,"D"));

        String str = new String( "A A 0.2051\n" +
                "B B 0.2051\n" +
                "C C 0.2051\n" +
                "D D 0.2051\n" +
                "A B 0.0513\n" +
                "A C 0.0\n" +
                "A D 0.0513\n" +
                "B C 0.0513\n" +
                "B D 0.0\n" +
                "C D 0.0256");

        Table<Long,Long> graph = new Table<Long,Long>();
        int communities = 4;
        for( int i = 0; i < communities; i+=1) {
            graph.add(new Tuple<>(0L+(i*5), 1L+(i*5)));
            graph.add(new Tuple<>(0L+(i*5), 2L+(i*5)));
            graph.add(new Tuple<>(1L+(i*5), 0L+(i*5)));
            graph.add(new Tuple<>(1L+(i*5), 2L+(i*5)));
            graph.add(new Tuple<>(1L+(i*5), 3L+(i*5)));
            graph.add(new Tuple<>(1L+(i*5), 4L+(i*5)));
            graph.add(new Tuple<>(2L+(i*5), 0L+(i*5)));
            graph.add(new Tuple<>(2L+(i*5), 1L+(i*5)));
            graph.add(new Tuple<>(2L+(i*5), 3L+(i*5)));
            graph.add(new Tuple<>(2L+(i*5), 4L+(i*5)));
            graph.add(new Tuple<>(3L+(i*5), 1L+(i*5)));
            graph.add(new Tuple<>(3L+(i*5), 2L+(i*5)));
            graph.add(new Tuple<>(3L+(i*5), 4L+(i*5)));
            graph.add(new Tuple<>(4L+(i*5), 1L+(i*5)));
            graph.add(new Tuple<>(4L+(i*5), 2L+(i*5)));
            graph.add(new Tuple<>(4L+(i*5), 3L+(i*5)));
        }

        graph.add(new Tuple<>(2L, 7L));
        graph.add(new Tuple<>(7L, 2L));
        graph.add(new Tuple<>(4L, 9L));
        graph.add(new Tuple<>(9L, 4L));

        graph.add(new Tuple<>(3L, 17L));
        graph.add(new Tuple<>(17L, 3L));
        graph.add(new Tuple<>(4L, 19L));
        graph.add(new Tuple<>(19L, 4L));

        graph.add(new Tuple<>(8L, 12L));
        graph.add(new Tuple<>(12L, 8L));
        graph.add(new Tuple<>(6L, 13L));
        graph.add(new Tuple<>(13L, 6L));

        graph.add(new Tuple<>(10L, 15L));
        graph.add(new Tuple<>(15L, 10L));

        assertTrue(graph.size() == 78);

        InputStream stream = new ByteArrayInputStream(str.getBytes());
        JointDistribution<String,String> distribution = new JointDistribution<>();
        distribution.load(stream," ", (String s) -> s, (String s) -> s );

        StochasticBlockModelMatching matching = new StochasticBlockModelMatching();
        Map<Long,Long> result = matching.run(graph, table, distribution);
        assertTrue(result.size() == 20);
    }
}
