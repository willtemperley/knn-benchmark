package net.robowiki.knn.implementations;

import net.robowiki.knn.util.KNNPoint;

/**
 * @author Nat Pavasant
 */
import nat.tree.PRKdBucketTree;
import nat.tree.PRKdBucketTree.*;
public class NatTreeKNNSearch extends KNNImplementation {
	private PRKdBucketTree<String> tree;

	public NatTreeKNNSearch(int dimension) {
		super(dimension);
		tree = PRKdBucketTree.getTree("", dimension, 1, 2, 500, 10, new PRKdBucketTree.Distancer() {
			@Override
			public double getPointDistance(double[] p1, double[] p2,
					double[] weight) {
				return getTestSuiteDistance(p1,p2);
			}
			
		});
	}

	@Override
	public void addPoint(double[] location, String value) {
		tree.addPoint(value, location);
	}

	@Override
	public KNNPoint[] getNearestNeighbors(double[] location, int size) {
		KdCluster<String> cluster = tree.getNearestNeighbors(size, location);
		KNNPoint[] data = new KNNPoint[size];
		int i = 0;
		for (KdPoint<String> p : cluster) {
			data[i++] = new KNNPoint(p.getValue(), p.getDistanceToCenter());
		}
		return data;
	}
	
	protected final double getTestSuiteDistance(double[] p1, double[] p2) {
		return getDistance(p1, p2);
	}

	@Override
	public String getName() {
		return "Nat's Bucket PR k-d tree";
	}
}
