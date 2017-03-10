package org.dama.datasynth.matching;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertTrue;

/**
 * Created by aprat on 3/03/17.
 */
public class IndexTest {

    private void helper(Index<String> countryIndex, String country, long begin, long num) {

        Set<Long> idSet = new HashSet<Long>();
        for(int i = 0; i < num; i+=1) {
            Long id = countryIndex.poll(country);
            assertTrue(id != null);
            idSet.add(id);
        }
        assertTrue(idSet.size() == num);
        for(long i = begin; i < begin+num; i+=1) {
            assertTrue(idSet.contains(i));
        }
    }

    @Test
    public void behaviorTest() {
        Table<Long,String> table = new Table<>();
        // 5 China
        table.add(new Tuple<>(0L,"China"));
        table.add(new Tuple<>(1L,"China"));
        table.add(new Tuple<>(2L,"China"));
        table.add(new Tuple<>(3L,"China"));
        table.add(new Tuple<>(4L,"China"));

        // 4 India
        table.add(new Tuple<>(5L,"India"));
        table.add(new Tuple<>(6L,"India"));
        table.add(new Tuple<>(7L,"India"));
        table.add(new Tuple<>(8L,"India"));

        // 3 USA
        table.add(new Tuple<>(9L,"USA"));
        table.add(new Tuple<>(10L,"USA"));
        table.add(new Tuple<>(11L,"USA"));

        // 2 Spain
        table.add(new Tuple<>(12L,"Spain"));
        table.add(new Tuple<>(13L,"Spain"));

        // 1 Catalonia
        table.add(new Tuple<>(14L,"Catalonia"));

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
