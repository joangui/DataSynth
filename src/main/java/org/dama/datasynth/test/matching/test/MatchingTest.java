package org.dama.datasynth.test.matching.test;

import org.dama.datasynth.test.matching.*;
import org.dama.datasynth.test.matching.Dictionary;

import java.io.FileInputStream;
import java.util.*;

/**
 * Created by aprat on 5/03/17.
 */
public class MatchingTest {

    public MatchingTest() {
    }

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
        //Collections.shuffle(edges);



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
        Table<String,String> newConnectedCountries = new Table<>();
        for(Tuple<Long,Long> edge : edges) {
            Long nodeTail = mapping.get(edge.getX());
            Long nodeHead = mapping.get(edge.getY());
            if(nodeTail != null && nodeHead != null) {
                String attrTail = personCountryDictionary.get(nodeTail);
                String attrHead = personCountryDictionary.get(nodeHead);
                if(attrTail.compareTo(attrHead) < 0) {
                    newConnectedCountries.add(new Tuple<>(attrTail,attrHead));
                } else {
                    newConnectedCountries.add(new Tuple<>(attrHead,attrTail));
                }
            }
        }

        JointDistribution<String,String> newCountryPairsDistribution = new JointDistribution<>();
        newCountryPairsDistribution.learn(newConnectedCountries);

        Comparator comparator = new Comparator<JointDistribution.Entry<String,String>>(){
            @Override
            public int compare(JointDistribution.Entry<String, String> o1, JointDistribution.Entry<String, String> o2) {
                if(o1.getProbability() < o2.getProbability()) {
                    return 1;
                }

                if(o1.getProbability() > o2.getProbability()) {
                    return -1;
                }
                return 0;
            }
        };

        ArrayList<JointDistribution.Entry<String,String>> countryPairsEntries = new ArrayList<>(countryPairsDistribution.getEntries());
        Collections.sort(countryPairsEntries,comparator);
        ArrayList<JointDistribution.Entry<String,String>> newCountryPairsEntries = new ArrayList<>(newCountryPairsDistribution.getEntries());
        Collections.sort(newCountryPairsEntries,comparator);

        for(int i = 0; i < 20; i+=1) {
            JointDistribution.Entry<String,String> originalEntry   = countryPairsEntries.get(i);
            JointDistribution.Entry<String,String> newEntry        = newCountryPairsEntries.get(i);
            System.out.print(originalEntry.getXvalue()+" "+originalEntry.getYvalue()+" "+originalEntry.getProbability()+" --- ");
            System.out.println(newEntry.getXvalue()+" "+newEntry.getYvalue()+" "+newEntry.getProbability());
        }
    }
}
