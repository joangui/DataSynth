package org.dama.datasynth.test.matching;

import org.dama.datasynth.matching.Distribution;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by aprat on 3/03/17.
 */
public class DistributionTest {


    @Test
    public void learnTest() {
        ArrayList<String> values = new ArrayList<>();
        values.add("Male");
        values.add("Male");
        values.add("Male");
        values.add("Male");
        values.add("Male");
        values.add("Male");
        values.add("Female");
        values.add("Female");
        values.add("Female");
        values.add("Female");

        Distribution<String> distribution = new Distribution<>();
        distribution.learn(values);

        Distribution.Entry<String> male = new Distribution.Entry<>("Male",0.6);
        Distribution.Entry<String> female = new Distribution.Entry<>("Female",0.4);
        List<Distribution.Entry<String>> entries = distribution.getEntries();
        assertTrue(entries.indexOf(male) != -1);
        assertTrue(entries.indexOf(female) != -1);
    }
}
