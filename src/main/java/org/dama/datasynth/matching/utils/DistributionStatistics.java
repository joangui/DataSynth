/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dama.datasynth.matching.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import org.apache.commons.math3.stat.inference.ChiSquareTest;
import org.apache.commons.math3.stat.inference.KolmogorovSmirnovTest;
import org.dama.datasynth.matching.JointDistribution;
import org.dama.datasynth.matching.Tuple;

/**
 *
 * @author joangui
 */
public class DistributionStatistics<X extends Comparable<X>, Y extends Comparable<Y>> {

	long[] observedFrequencies;
	double[] expectedFrequencies;
	private final JointDistribution<X, Y> observed;
	private final JointDistribution<X, Y> expected;

	public DistributionStatistics(JointDistribution<X, Y> expected, JointDistribution<X, Y> observed, long sampleSize) {

		this.expected = expected;
		this.observed = observed;
		System.out.println("#edges: " + sampleSize);
		System.out.println("sample size 1%:" + (long) (sampleSize * .01));
		sampleSize *= .01;
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

		expectedFrequencies = new double[expectedEntries.size()];
		for (int i = 0; i < expectedEntries.size(); ++i) {
			expectedFrequencies[i] = (sampleSize * expectedEntries.get(i).getValue());
		}

		observedFrequencies = new long[observedEntries.size()];
		for (int i = 0; i < observedEntries.size(); ++i) {
			observedFrequencies[i] = (long) (sampleSize * observedEntries.get(i).getValue());
		}
	}

	public <X extends Comparable<X>, Y extends Comparable<Y>> double chiSquareTest() {

		ChiSquareTest chiSquareTest = new ChiSquareTest();
		return chiSquareTest.chiSquareTest(expectedFrequencies, observedFrequencies);

	}

	@Deprecated
	public <X extends Comparable<X>, Y extends Comparable<Y>> double kolmogorovSmirnovTest() {
		KolmogorovSmirnovTest kst = new KolmogorovSmirnovTest();
		return kst.kolmogorovSmirnovTest(expectedFrequencies, expectedFrequencies);
	}

	public <X extends Comparable<X>, Y extends Comparable<Y>> double dMax(){
		
	}

}
