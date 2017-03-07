package org.dama.datasynth.test.matching.test;

import org.dama.datasynth.test.matching.*;
import org.dama.datasynth.test.matching.Dictionary;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by aprat on 5/03/17.
 */
public class MatchingCommunityTest {

	public MatchingCommunityTest() {
	}

	static public void main(String[] args) {
		FileInputStream fileInputStream1 = null;
		FileInputStream fileInputStream2 = null;
		try {
			fileInputStream1 = new FileInputStream(args[0]);

		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
			System.exit(1);
		}
		try {
			fileInputStream2 = new FileInputStream(args[1]);

		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
			System.exit(1);

		}

		Table<Long, String> attributes = new Table<>();
		try {
			attributes.load(fileInputStream1, " ", (String s) -> Long.parseLong(s), (String s) -> s);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		Table<Long, Long> edges = new Table<>();
		// Load edges table
		try {
			edges.load(fileInputStream2, " ", (String s) -> Long.parseLong(s), (String s) -> Long.parseLong(s));
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		run(attributes, edges);

	}

	static public <XType extends Comparable<XType>> void run(Table<Long, XType> attributes, Table<Long, Long> edges) {

		Dictionary<Long, XType> attributesDictionary = new Dictionary<>(attributes);

		System.out.println("Learning attribute pairs joint distribution");
		// Extract connected attributes joint distribution
		Table<XType, XType> connectedAttributes = new Table<>();
		for (Tuple<Long, Long> edge : edges) {
			XType attributeX = attributesDictionary.get(edge.getX());
			XType attributeY = attributesDictionary.get(edge.getY());
			if (attributeX.compareTo(attributeY) < 0) {
				connectedAttributes.add(new Tuple<>(attributeX, attributeY));
			} else {
				connectedAttributes.add(new Tuple<>(attributeY, attributeX));
			}
		}

		JointDistribution<XType, XType> attributesDistribution = new JointDistribution<>();
		attributesDistribution.learn(connectedAttributes);

		System.out.println("Executing matching algorithm");
		Map<Long, Long> mapping = Matching.run(edges, attributes, attributesDistribution);
		System.out.println("Size of the mapping: " + mapping.size());
		System.out.println("Size of the attribute table: " + attributes.size());
		Table<XType, XType> newConnectedAttributes = new Table<>();
		for (Tuple<Long, Long> edge : edges) {
			Long nodeTail = mapping.get(edge.getX());
			Long nodeHead = mapping.get(edge.getY());
			if (nodeTail != null && nodeHead != null) {
				XType attrTail = attributesDictionary.get(nodeTail);
				XType attrHead = attributesDictionary.get(nodeHead);
				if (attrTail.compareTo(attrHead) < 0) {
					newConnectedAttributes.add(new Tuple<>(attrTail, attrHead));
				} else {
					newConnectedAttributes.add(new Tuple<>(attrHead, attrTail));
				}
			}
		}

		JointDistribution<XType, XType> newAttributesDistribution = new JointDistribution<>();
		newAttributesDistribution.learn(newConnectedAttributes);

		Comparator comparator = new Comparator<JointDistribution.Entry<XType, XType>>() {
			@Override
			public int compare(JointDistribution.Entry<XType, XType> o1, JointDistribution.Entry<XType, XType> o2) {
				if (o1.getProbability() < o2.getProbability()) {
					return 1;
				}

				if (o1.getProbability() > o2.getProbability()) {
					return -1;
				}
				return 0;
			}
		};

		ArrayList<JointDistribution.Entry<XType, XType>> attributesPairsEntries = new ArrayList<>(attributesDistribution.getEntries());
		Collections.sort(attributesPairsEntries, comparator);
		ArrayList<JointDistribution.Entry<XType, XType>> newAttributesPairsEntries = new ArrayList<>(newAttributesDistribution.getEntries());
		Collections.sort(newAttributesPairsEntries, comparator);

		for (int i = 0; i < attributesPairsEntries.size() && i < 20; i += 1) {
			JointDistribution.Entry<XType, XType> originalEntry = attributesPairsEntries.get(i);
			JointDistribution.Entry<XType, XType> newEntry = newAttributesPairsEntries.get(i);
			System.out.print(originalEntry.getXvalue() + " " + originalEntry.getYvalue() + " " + originalEntry.getProbability() + " --- ");
			System.out.println(newEntry.getXvalue() + " " + newEntry.getYvalue() + " " + newEntry.getProbability());
		}

		System.out.println("\nChi-Square: "+chiSquareTest(attributesDistribution, newAttributesDistribution, edges));
	}

	static public  double chiSquareTest(JointDistribution observed, JointDistribution calculated,Table<Long, Long> edges) {

		Set<Long> nodes = new HashSet<>();
		for(Tuple<Long,Long> t:edges)
		{
			nodes.add(t.getX());
			nodes.add(t.getY());
		}

		double chiSquareTest = 0D;
		for(int i = 0; i< observed.size();i++)
		{
			JointDistribution.Entry observedEntry = observed.get(i);
			Tuple observedTuple = observedEntry.getTuple();
			
			double observedProbability = observedEntry.getProbability();
			double calculatedProbability = calculated.getProbability(observedTuple);

			double observedOccurrences = observedProbability*nodes.size();
			double calculatedOccurrences = calculatedProbability*nodes.size();

			chiSquareTest+=Math.pow(calculatedOccurrences-observedOccurrences,2)/observedOccurrences;
			
		}

		

		return chiSquareTest;
	}
}
