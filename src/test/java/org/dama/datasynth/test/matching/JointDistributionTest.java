package org.dama.datasynth.test.matching;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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
        JointDistribution.Entry<String,String> maleFemale = new JointDistribution.Entry<>("Male","Female",0.7);
        JointDistribution.Entry<String,String> maleMale = new JointDistribution.Entry<>("Male","Male",0.15);
        JointDistribution.Entry<String,String> femaleFemale = new JointDistribution.Entry<>("Female","Female",0.15);
        List<JointDistribution.Entry<String,String>> entries = distribution.getEntries();
        assertTrue(entries.indexOf(maleMale) != -1);
        assertTrue(entries.indexOf(maleFemale) != -1);
        assertTrue(entries.indexOf(femaleFemale) != -1);
    }

    @Test
    public void learnTest() {
        ArrayList<Tuple<String,String>> pairs = new ArrayList<>();
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

        JointDistribution.Entry<String,String> maleFemale = new JointDistribution.Entry<>("Male","Female",0.8);
        JointDistribution.Entry<String,String> maleMale = new JointDistribution.Entry<>("Male","Male",0.1);
        JointDistribution.Entry<String,String> femaleFemale = new JointDistribution.Entry<>("Female","Female",0.1);
        List<JointDistribution.Entry<String,String>> entries = distribution.getEntries();
        assertTrue(entries.indexOf(maleMale) != -1);
        assertTrue(entries.indexOf(maleFemale) != -1);
        assertTrue(entries.indexOf(femaleFemale) != -1);
    }
}
