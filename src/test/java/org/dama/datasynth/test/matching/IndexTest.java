package org.dama.datasynth.test.matching;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertTrue;

/**
 * Created by aprat on 3/03/17.
 */
public class IndexTest {

    private void helper(Index<String> countryIndex, String country, int begin, int num) {

        Set<Integer> idSet = new HashSet<Integer>();
        for(int i = 0; i < num; i+=1) {
            Integer id = countryIndex.poll(country);
            assertTrue(id != null);
            idSet.add(id);
        }
        assertTrue(idSet.size() == num);
        for(int i = begin; i < begin+num; i+=1) {
            assertTrue(idSet.contains(i));
        }

    }

    @Test
    public void behaviorTest() {
        ArrayList<String> table = new ArrayList<String>();
        // 5 China
        table.add("China");
        table.add("China");
        table.add("China");
        table.add("China");
        table.add("China");

        // 4 India
        table.add("India");
        table.add("India");
        table.add("India");
        table.add("India");

        // 3 USA
        table.add("USA");
        table.add("USA");
        table.add("USA");

        // 2 Spain
        table.add("Spain");
        table.add("Spain");

        // 1 Catalonia
        table.add("Catalonia");

        Index<String> countryIndex = new Index(table);
        assertTrue(countryIndex.values().size() == 5);
        helper(countryIndex, "China",0,5);
        assertTrue(countryIndex.values().size() == 4);
        helper(countryIndex, "India",5,4);
        assertTrue(countryIndex.values().size() == 3);
        helper(countryIndex, "USA",9,3);
        assertTrue(countryIndex.values().size() == 2);
        helper(countryIndex, "Spain",12,2);
        assertTrue(countryIndex.values().size() == 1);
        helper(countryIndex, "Catalonia",14,1);
        assertTrue(countryIndex.values().size() == 0);

    }
}
