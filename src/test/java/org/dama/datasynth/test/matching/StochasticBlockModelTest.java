package org.dama.datasynth.test.matching;

import org.dama.datasynth.matching.JointDistribution;
import org.dama.datasynth.matching.StochasticBlockModel;
import org.dama.datasynth.matching.Table;
import org.dama.datasynth.matching.Tuple;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

/**
 * Created by aprat on 10/03/17.
 */
public class StochasticBlockModelTest {

    @Test
    public void testStochasticBlockModel() {
        Map<String,Integer> mapping = new HashMap<>();
        mapping.put("A",0);
        mapping.put("B",1);
        mapping.put("C",2);
        mapping.put("D",3);

        long [] sizes = {5L,5L,5L,5L};

        double [][] probabilities = new double[4][4];
        Arrays.fill(probabilities[0],0.0D);
        Arrays.fill(probabilities[1],0.0D);
        Arrays.fill(probabilities[2],0.0D);
        Arrays.fill(probabilities[3],0.0D);

        probabilities[0][0] = 0.79989D;
        probabilities[1][1] = 0.79989D;
        probabilities[2][2] = 0.79989D;
        probabilities[3][3] = 0.79989D;

        probabilities[0][1] = 0.08D;
        probabilities[0][2] = 0.0D;
        probabilities[0][3] = 0.08D;

        probabilities[1][2] = 0.08D;
        probabilities[1][3] = 0.0;

        probabilities[2][3] = 0.04;

        StochasticBlockModel<String> model = new StochasticBlockModel<>(mapping,sizes,probabilities);

        assertTrue(model.getNumBlocks() == 4);
        assertTrue(model.getSize("A") == 5);
        assertTrue(model.getSize("B") == 5);
        assertTrue(model.getSize("C") == 5);
        assertTrue(model.getSize("D") == 5);
        assertTrue(model.getProbability("A","A") == 0.79989D);
        assertTrue(model.getProbability("B","B") == 0.79989D);
        assertTrue(model.getProbability("C","C") == 0.79989D);
        assertTrue(model.getProbability("D","D") == 0.79989D);

        assertTrue(model.getProbability("A","B") == 0.08D);
        assertTrue(model.getProbability("A","C") == 0.0D);
        assertTrue(model.getProbability("A","D") == 0.08);

        assertTrue(model.getProbability("B","C") == 0.08D);
        assertTrue(model.getProbability("B","D") == 0.0D);

        assertTrue(model.getProbability("C","D") == 0.04D);

    }

    @Test
    public void testExtract() {

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

        InputStream stream = new ByteArrayInputStream(str.getBytes());
        JointDistribution<String,String> distribution = new JointDistribution<>();
        distribution.load(stream," ", (String s) -> s, (String s) -> s );

        StochasticBlockModel<String> model = StochasticBlockModel.extractFrom(39L,table,distribution);

        assertTrue(model.getNumBlocks() == 4);
        assertTrue(model.getSize("A") == 5);
        assertTrue(model.getSize("B") == 5);
        assertTrue(model.getSize("C") == 5);
        assertTrue(model.getSize("D") == 5);
        assertTrue(Math.abs(model.getProbability("A","A") - 0.79989D) < 0.0001);
        assertTrue(Math.abs(model.getProbability("B","B") - 0.79989D) < 0.0001);
        assertTrue(Math.abs(model.getProbability("C","C") - 0.79989D) < 0.0001);
        assertTrue(Math.abs(model.getProbability("D","D") - 0.79989D) < 0.0001);

        assertTrue(Math.abs(model.getProbability("A","B") - 0.08D) < 0.0001);
        assertTrue(Math.abs(model.getProbability("A","C") - 0.0D) < 0.0001);
        assertTrue(Math.abs(model.getProbability("A","D") - 0.08D) < 0.0001);

        assertTrue(Math.abs(model.getProbability("B","C") - 0.08D) < 0.0001);
        assertTrue(Math.abs(model.getProbability("B","D") - 0.0D) < 0.0001);

        assertTrue(Math.abs(model.getProbability("C","D") - 0.04D) < 0.0001);

    }
}
