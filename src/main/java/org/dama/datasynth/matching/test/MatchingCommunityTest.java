package org.dama.datasynth.matching.test;

import org.apache.commons.math3.stat.inference.ChiSquareTest;
import org.dama.datasynth.matching.Dictionary;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

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
		Matching matching = new MatchingGreedy();
		Map<Long, Long> mapping = matching.run(edges, attributes, attributesDistribution);
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

		Comparator comparator = new Comparator<JointDistribution.Entry<Tuple<XType, XType>, Double>>() {
			@Override
			public int compare(JointDistribution.Entry<Tuple<XType, XType>, Double> o1, JointDistribution.Entry<Tuple<XType, XType>, Double> o2) {
				if (o1.getValue() < o2.getValue()) {
					return 1;
				}

				if (o1.getValue() > o2.getValue()) {
					return -1;
				}
				return 0;
			}
		};

		
		ArrayList<JointDistribution.Entry<Tuple<XType, XType>, Double>> attributesPairsEntries = new ArrayList<>(attributesDistribution.getEntries());
		Collections.sort(attributesPairsEntries, comparator);
		ArrayList<JointDistribution.Entry<Tuple<XType, XType>, Double>> newAttributesPairsEntries = new ArrayList<>(newAttributesDistribution.getEntries());
		Collections.sort(newAttributesPairsEntries, comparator);

		for (int i = 0; i < attributesPairsEntries.size() && i < 20; i += 1) {
			JointDistribution.Entry<Tuple<XType, XType>, Double> originalEntry = attributesPairsEntries.get(i);
			JointDistribution.Entry<Tuple<XType, XType>, Double> newEntry = newAttributesPairsEntries.get(i);
			System.out.print(originalEntry.getKey().getX() + " " + originalEntry.getKey().getY() + " " + originalEntry.getValue() + " --- ");
			System.out.println(newEntry.getKey().getX() + " " + newEntry.getKey().getY() + " " + newEntry.getValue());
		}

		System.out.println("\nChi-Square: " + chiSquareTest(attributesDistribution, newAttributesDistribution, edges.size()));
	}

	static public <X extends Comparable<X>, Y extends Comparable<Y>> double chiSquareTest(JointDistribution<X, Y> expected, JointDistribution<X, Y> observed, long size) {

		System.out.println("#edges: "+size);
		System.out.println("sample size 1%:"+(long)(size*.01));
		size*=.01;
		Comparator comparator = new Comparator<JointDistribution.Entry<Tuple<X, Y>, Double>>() {
			@Override
			public int compare(JointDistribution.Entry<Tuple<X, Y>, Double> o1, JointDistribution.Entry<Tuple<X, Y>, Double> o2) {
				return o1.getKey().compareTo(o2.getKey());
			}
		};

		ArrayList<JointDistribution.Entry<Tuple<X, Y>, Double>> expectedEntries = new ArrayList<>(expected.getEntries());
		Collections.sort(expectedEntries, comparator);
		ArrayList<JointDistribution.Entry<Tuple<X, Y>, Double>> observedEntries = new ArrayList<>(observed.getEntries());
		Collections.sort(observedEntries, comparator);

		double[] expectedFrequencies = new double[expectedEntries.size()];
		for (int i = 0; i < expectedEntries.size(); ++i) {
			expectedFrequencies[i] = (size * expectedEntries.get(i).getValue());
		}

		long[] observedFrequencies = new long[observedEntries.size()];
		for (int i = 0; i < observedEntries.size(); ++i) {
			observedFrequencies[i] = (long) (size * observedEntries.get(i).getValue());
		}

        ChiSquareTest chiSquareTest = new ChiSquareTest();
		return chiSquareTest.chiSquareTest(expectedFrequencies,observedFrequencies);

	}
}
