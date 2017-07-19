package org.dama.datasynth.runtime.spark.operators.matching;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.Assert.assertTrue;

/**
 * Created by aprat on 3/03/17.
 */
public class JointDistributionTest {

    @Test
    public void loadFileTest() {
        String str = new String( "Male Female 0.7\n" +
                                 "Male Male 0.15\n" +
                                 "Female Female 0.15");

        InputStream stream = new ByteArrayInputStream(str.getBytes());
        JointDistribution<String,String> distribution = new JointDistribution<>();
        distribution.load(stream," ", (String s) -> s, (String s) -> s );

        Tuple<String,String> maleFemale = new Tuple<String,String>("Male","Female");
        Tuple<String,String> maleMale = new Tuple<>("Male","Male");
        Tuple<String,String> femaleFemale = new Tuple("Female","Female");
        assertTrue(distribution.getProbability(maleFemale) == 0.7D);
        assertTrue(distribution.getProbability(maleMale) == 0.15D);
        assertTrue(distribution.getProbability(femaleFemale) == 0.15D);
    }

    @Test
    public void learnTest() {
        Table<String,String> pairs = new Table<>();
        pairs.add(new Tuple<>("Male","Female"));
        pairs.add(new Tuple<>("Male","Female"));
        pairs.add(new Tuple<>("Male","Female"));
        pairs.add(new Tuple<>("Male","Female"));
        pairs.add(new Tuple<>("Male","Female"));
        pairs.add(new Tuple<>("Male","Female"));
        pairs.add(new Tuple<>("Male","Female"));
        pairs.add(new Tuple<>("Male","Female"));
        pairs.add(new Tuple<>("Male","Male"));
        pairs.add(new Tuple<>("Female","Female"));

        JointDistribution<String,String> distribution = new JointDistribution<>();
        distribution.learn(pairs);

        Tuple<String,String> maleFemale = new Tuple<String,String>("Male","Female");
        Tuple<String,String> maleMale = new Tuple<>("Male","Male");
        Tuple<String,String> femaleFemale = new Tuple("Female","Female");
        assertTrue(distribution.getProbability(maleFemale) == 0.8D);
        assertTrue(distribution.getProbability(maleMale) == 0.1D);
        assertTrue(distribution.getProbability(femaleFemale) == 0.1D);
    }
}
