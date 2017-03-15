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

	public DistributionStatistics(JointDistribution<X, Y> expected, JointDistribution<X, Y> observed) {

		this.expected = expected;
		this.observed = observed;
	}

	public double chiSquareTest(long sampleSize) {

		Comparator comparator = new Comparator<JointDistribution.Entry<Tuple<X, Y>, Double>>() {
			private JointDistribution<X, Y> distribution = null;

			@Override
			public int compare(JointDistribution.Entry<Tuple<X, Y>, Double> o1, JointDistribution.Entry<Tuple<X, Y>, Double> o2) {
				return o1.getKey().compareTo(o2.getKey());
			}

			public void setDistribution(JointDistribution<X, Y> distribution) {
				this.distribution = distribution;
			}
		};

		Set<Tuple<X, Y>> entries = new TreeSet<>();
		entries.addAll(expected.keySet());
		entries.addAll(observed.keySet());

		double[] expectedFrequencies = new double[entries.size()];
		int i = 0;
		for (Tuple<X, Y> tuple : entries) {
			expectedFrequencies[i] = (sampleSize * expected.getProbability(tuple));
			i += 1;
		}

		i = 0;
		long[] observedFrequencies = new long[entries.size()];
		for (Tuple<X, Y> tuple : entries) {
			observedFrequencies[i] = (long) (sampleSize * observed.getProbability(tuple));
			i += 1;
		}

		ChiSquareTest chiSquareTest = new ChiSquareTest();
		return chiSquareTest.chiSquareTest(expectedFrequencies, observedFrequencies);

	}

	public DMaxStatistics dMaxTest() {
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

		ArrayList<Double> accumulativeExpectedProbValues = new ArrayList<>();
		ArrayList<Double> accumulativeObservedProbValues = new ArrayList<>();

		double dMax = 0;
		for (int i = 0; i < expectedEntries.size(); i++) {
			JointDistribution.Entry<Tuple<X, Y>, Double> expectedEntry = expectedEntries.get(i);
			double expectedProb = expectedEntry.getValue();
			accumulativeExpectedProb += expectedProb;
			accumulativeExpectedProbValues.add(accumulativeExpectedProb);

			double observedProb = observed.get(expectedEntry.getKey());
			accumulativeObservedProb += observedProb;
			accumulativeObservedProbValues.add(accumulativeObservedProb);

			dMax = Math.abs(accumulativeExpectedProb - accumulativeObservedProb) > dMax ? Math.abs(accumulativeExpectedProb - accumulativeObservedProb) : dMax;
		}

//		System.out.println();
//		System.out.println(accumulativeObservedProbValues.toString().replace("[", "").replaceAll("]", "").replaceAll(", ", ","));

		return new DMaxStatistics(dMax, accumulativeExpectedProbValues, accumulativeObservedProbValues);
	}

	public RsquareStatistics rSquareTest() {
		Comparator comparator = new Comparator<JointDistribution.Entry<Tuple<X, Y>, Double>>() {
			@Override
			public int compare(JointDistribution.Entry<Tuple<X, Y>, Double> o1, JointDistribution.Entry<Tuple<X, Y>, Double> o2) {
				return -1 * o1.getValue().compareTo(o2.getValue());
			}
		};

		ArrayList<JointDistribution.Entry<Tuple<X, Y>, Double>> expectedEntries = new ArrayList<>(expected.getEntries());
		Collections.sort(expectedEntries, comparator);

		ArrayList<Double> expectedProbValues = new ArrayList<>();
		ArrayList<Double> observedProbValues = new ArrayList<>();

		double rSquare = 0;
		for (int i = 0; i < expectedEntries.size(); i++) {
			JointDistribution.Entry<Tuple<X, Y>, Double> expectedEntry = expectedEntries.get(i);
			double expectedProb = expectedEntry.getValue();
			expectedProbValues.add(expectedProb);

			double observedProb = observed.get(expectedEntry.getKey());
			observedProbValues.add(observedProb);

			rSquare += Math.pow((expectedProb - observedProb),2);
		}
		rSquare/=expectedProbValues.size();

		return new RsquareStatistics(rSquare, expectedProbValues, observedProbValues);
	
	}

	

	public static class DMaxStatistics {

		public double dMaxValue;
		public ArrayList<Double> accumulativeExpectedProbValues;
		public ArrayList<Double> accumulativeObservedProbValues;

		public DMaxStatistics(double dMaxValue, ArrayList<Double> accumulativeExpectedProbValues, ArrayList<Double> accumulativeObservedProbValues) {
			this.dMaxValue = dMaxValue;
			this.accumulativeExpectedProbValues = accumulativeExpectedProbValues;
			this.accumulativeObservedProbValues = accumulativeObservedProbValues;
		}
	}
	public static class RsquareStatistics {

		public double rSquare;
		public ArrayList<Double> expectedProbValues;
		public ArrayList<Double> observedProbValues;

		public RsquareStatistics(double rSquare, ArrayList<Double> expectedProbValues, ArrayList<Double> observedProbValues) {
			this.rSquare = rSquare;
			this.expectedProbValues = expectedProbValues;
			this.observedProbValues = observedProbValues;
		}
	}

}
