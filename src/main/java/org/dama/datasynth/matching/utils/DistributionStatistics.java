/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dama.datasynth.matching.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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
			expectedFrequencies[i] = (sampleSize * expectedEntries.get(i).getValue());
		}

		long[] observedFrequencies = new long[observedEntries.size()];
		for (int i = 0; i < observedEntries.size(); ++i) {
			observedFrequencies[i] = (long) (sampleSize * observedEntries.get(i).getValue());
		}

		ChiSquareTest chiSquareTest = new ChiSquareTest();
		return chiSquareTest.chiSquareTest(expectedFrequencies, observedFrequencies);

	}

	public double dMaxTest() {
		Comparator comparator = new Comparator<JointDistribution.Entry<Tuple<X, Y>, Double>>() {
			@Override
			public int compare(JointDistribution.Entry<Tuple<X, Y>, Double> o1, JointDistribution.Entry<Tuple<X, Y>, Double> o2) {
				return -1*(o1.getKey().compareTo(o2.getKey()));
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
