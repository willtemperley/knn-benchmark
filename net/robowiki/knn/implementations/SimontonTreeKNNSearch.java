package net.robowiki.knn.implementations;

import net.robowiki.knn.util.KNNPoint;

/**
 * @author Nat Pavasant
 */
import simonton.utils.*;
public class SimontonTreeKNNSearch extends KNNImplementation {
	private MyTree<String> tree;

	public SimontonTreeKNNSearch(int dimension) {
		super(dimension);
		tree = new MyTree<String>(dimension, 8, 1, 500);
	}

	@Override
	public void addPoint(double[] location, String value) {
		tree.add(location, value);
	}

	@Override
	public KNNPoint[] getNearestNeighbors(double[] location, int size) {
		Cluster<String> cluster = tree.buildCluster(location, size, new Distancer() {
			public double getDistance(double[] d1, double[] d2) {
				return getTestSuiteDistance(d1, d2);
			}
		});
		KNNPoint[] data = new KNNPoint[size];
		int i = 0;
		for (Cluster.Point<String> p : cluster) {
			data[i++] = new KNNPoint(p.value, p.distanceToCenter);
		}
		return data;
	}
	
	protected final double getTestSuiteDistance(double[] p1, double[] p2) {
		return getDistance(p1, p2);
	}

	@Override
	public String getName() {
		return "Simonton's Bucket PR k-d tree";
	}
}
