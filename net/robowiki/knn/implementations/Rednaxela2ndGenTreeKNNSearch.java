package net.robowiki.knn.implementations;

import java.util.List;

import ags.utils.dataStructures.trees.secondGenKD.KdTree;
import net.robowiki.knn.util.KNNPoint;

/**
 * @author Alex Schultz
 */
public class Rednaxela2ndGenTreeKNNSearch extends KNNImplementation {
    KdTree<String> tree;
	
	public Rednaxela2ndGenTreeKNNSearch(int dimension) {
		super(dimension);
		this.tree = new KdTree.SqrEuclid<String>(dimension,null); 
	}

    @Override
	public void addPoint(double[] location, String value) {
		tree.addPoint(location, value);
	}

	@Override
	public String getName() {
		return "Rednaxela's kd-tree (2nd gen)";
	}

	@Override
	public KNNPoint[] getNearestNeighbors(double[] location, int size) {
		List<KdTree.Entry<String>> list = tree.nearestNeighbor(location, size, false);
		
		KNNPoint[] points = new KNNPoint[list.size()];
		int i = 0;
		for (KdTree.Entry<String> entry : list) {
			points[i] = new KNNPoint(entry.value, entry.distance);
			i++;
		}
		
		return points;
	}

}
