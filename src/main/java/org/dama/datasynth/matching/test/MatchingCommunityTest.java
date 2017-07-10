package org.dama.datasynth.matching.test;

import org.dama.datasynth.matching.*;
import org.dama.datasynth.matching.utils.DistributionStatistics;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

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

	static public <XType extends Comparable<XType>> DistributionStatistics run(Table<Long, XType> attributes, Table<Long, Long> edges) {

		Dictionary<Long, XType> attributesDictionary = new Dictionary<>(attributes);

		System.out.println("Learning attribute pairs joint distribution");
		// Extract connected attributes joint distribution
		Table<XType, XType> connectedAttributes = new Table<>();
		for (Tuple<Long, Long> edge : edges) {
			if (edge.getX() < edge.getY()) {
				XType attributeX = attributesDictionary.get(edge.getX());
				XType attributeY = attributesDictionary.get(edge.getY());
				if (attributeX.compareTo(attributeY) < 0) {
					connectedAttributes.add(new Tuple<>(attributeX, attributeY));
				} else {
					connectedAttributes.add(new Tuple<>(attributeY, attributeX));
				}
			}
		}

		JointDistribution<XType, XType> attributesDistribution = new JointDistribution<>();
		attributesDistribution.learn(connectedAttributes);

		System.out.println("Executing org.dama.datasynth.matching algorithm");
//		Matching org.dama.datasynth.matching = new GreedyMatching();
		Matching matching = new StochasticBlockModelMatching();
		long start = System.currentTimeMillis();
		Map<Long, Long> mapping = matching.run(edges, attributes, attributesDistribution);
		long executionTime = System.currentTimeMillis() - start;
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
		DistributionStatistics ds = new DistributionStatistics(attributesDistribution, newAttributesDistribution);


		System.out.println("\nEXECUTION TIME: "+executionTime+" milliseconds");
		return ds;
	}
}
