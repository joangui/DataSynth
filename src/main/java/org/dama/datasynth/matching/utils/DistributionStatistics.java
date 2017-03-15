/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dama.datasynth.matching.utils;

import java.util.*;

import org.apache.commons.math3.stat.inference.ChiSquareTest;
import org.dama.datasynth.matching.JointDistribution;
import org.dama.datasynth.matching.Tuple;

/**
 *
 * @author joangui
 */
public class DistributionStatistics<X extends Comparable<X>, Y extends Comparable<Y>> {

	private final JointDistribution<X, Y> expected;
	private final JointDistribution<X, Y> observed;
	private final long sampleSize;

	public DistributionStatistics(JointDistribution<X, Y> expected, JointDistribution<X, Y> observed, long size) {

		this.expected = expected;
		this.observed = observed;
		this.sampleSize = size;
	}

	public double chiSquareTest() {

		Comparator comparator = new Comparator<JointDistribution.Entry<Tuple<X, Y>, Double>>() {
			private JointDistribution<X,Y> distribution = null;
			@Override
			public int compare(JointDistribution.Entry<Tuple<X, Y>, Double> o1, JointDistribution.Entry<Tuple<X, Y>, Double> o2) {
				return o1.getKey().compareTo(o2.getKey());
			}

			public void setDistribution(JointDistribution<X,Y> distribution) { this.distribution = distribution;}
		};

		Set<Tuple<X,Y>> entries = new TreeSet<>();
		entries.addAll(expected.keySet());
		entries.addAll(observed.keySet());

		double[] expectedFrequencies = new double[entries.size()];
		int i = 0;
		for (Tuple<X,Y> tuple : entries) {
			expectedFrequencies[i] = (sampleSize * expected.getProbability(tuple));
			i+=1;
		}

		i = 0;
		long[] observedFrequencies = new long[entries.size()];
		for (Tuple<X,Y> tuple : entries) {
			observedFrequencies[i] = (long)(sampleSize * observed.getProbability(tuple));
			i+=1;
		}

		ChiSquareTest chiSquareTest = new ChiSquareTest();
		return chiSquareTest.chiSquareTest(expectedFrequencies, observedFrequencies);

	}

	public double dMaxTest() {
		Comparator comparator = new Comparator<JointDistribution.Entry<Tuple<X, Y>, Double>>() {
			@Override
			public int compare(JointDistribution.Entry<Tuple<X, Y>, Double> o1, JointDistribution.Entry<Tuple<X, Y>, Double> o2) {
				return -1*o1.getValue().compareTo(o2.getValue());
			}
		};

		ArrayList<JointDistribution.Entry<Tuple<X, Y>, Double>> expectedEntries = new ArrayList<>(expected.getEntries());
		Collections.sort(expectedEntries, comparator);

		double accumulativeExpectedProb = 0;
		double accumulativeObservedProb = 0;

		List<Double> accumulativeExpectedProbValues = new ArrayList<>();
		List<Double> accumulativeObservedProbValues = new ArrayList<>();

		double dMax=0;
		for (int i = 0; i < expectedEntries.size(); i++) {
			JointDistribution.Entry<Tuple<X, Y>, Double> expectedEntry = expectedEntries.get(i);
			double expectedProb = expectedEntry.getValue();
			accumulativeExpectedProb += expectedProb;
			accumulativeExpectedProbValues.add(accumulativeExpectedProb);

			double observedProb = observed.get(expectedEntry.getKey());
			accumulativeObservedProb += observedProb;
			accumulativeObservedProbValues.add(accumulativeObservedProb);

			dMax = Math.abs(accumulativeExpectedProb-accumulativeObservedProb) > dMax?Math.abs(accumulativeExpectedProb-accumulativeObservedProb):dMax;
		}

		System.out.println("Expected: "+accumulativeExpectedProbValues);
		System.out.println("Observed: "+accumulativeObservedProbValues);
		return dMax;
	}
}
