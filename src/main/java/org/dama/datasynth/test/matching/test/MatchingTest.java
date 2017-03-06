package org.dama.datasynth.test.matching.test;

import org.dama.datasynth.test.matching.*;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by aprat on 5/03/17.
 */
public class MatchingTest {

    static public void main(String [] args) {

        Table<Long,String> personCountry = new Table<>();
        // Load person to country table
        System.out.println("Loading attributes table");
        try {
            personCountry.load(new FileInputStream(args[0]), " ", (String s) -> Long.parseLong(s), (String s) -> s );
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        Dictionary<Long,String> personCountryDictionary = new Dictionary<>(personCountry);


        System.out.println("Loading Edges table");
        Table<Long,Long> edges = new Table<>();
        // Load edges table
        try {
            edges.load(new FileInputStream(args[1]), " ", (String s) -> Long.parseLong(s), (String s) -> Long.parseLong(s));
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("Shuffling Edges table");
        Collections.shuffle(edges);



        System.out.println("Learning country pairs joint distribution");
        // Extract connected countries joint distribution
        Table<String,String>  connectedCountries = new Table<String,String>();
        for(Tuple<Long,Long> edge : edges) {
            String countryX = personCountryDictionary.get(edge.getX());
            String countryY = personCountryDictionary.get(edge.getY());
            if(countryX.compareTo(countryY) < 0) {
                connectedCountries.add(new Tuple<>(countryX,countryY));
            } else {
                connectedCountries.add(new Tuple<>(countryY,countryX));
            }
        }

        JointDistribution<String,String> countryPairsDistribution = new JointDistribution<>();
        countryPairsDistribution.learn(connectedCountries);

        System.out.println("Executing matching algorithm");
        Map<Long,Long> mapping = Matching.run(edges,personCountry,countryPairsDistribution);
        System.out.println("Size of the mapping: "+mapping.size());
        System.out.println("Size of the attribute table: "+personCountry.size());

    }
}
