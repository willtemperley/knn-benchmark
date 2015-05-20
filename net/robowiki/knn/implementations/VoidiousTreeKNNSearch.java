package net.robowiki.knn.implementations;

import java.util.HashMap;

import voidious.utils.KdBucketTree;
import net.robowiki.knn.util.KNNPoint;

/**
 * @author Nat Pavasant & Alex Schultz
 */
public class VoidiousTreeKNNSearch extends KNNImplementation {
	private KdBucketTree tree;
	private final HashMap<double[], String> data = new HashMap<double[], String>();

	public VoidiousTreeKNNSearch(int dimension) {
		super(dimension);
		tree = new KdBucketTree(dimension);
	}

	@Override
	public void addPoint(double[] location, String value) {
		data.put(location, value);
		tree.insert(location);
	}

	@Override
	public KNNPoint[] getNearestNeighbors(double[] location, int size) {
		double[][] r = KdBucketTree.nearestNeighbors(tree, location, size);
		
		KNNPoint[] arr = new KNNPoint[size];
		
		int i = 0;
		for (double[] a : r) {
			arr[i++] = new KNNPoint(data.get(a), 0); 
		}
		
		return arr;
	}

	@Override
	public String getName() {
		return "Voidious' Bucket PR k-d tree";
	}

}
