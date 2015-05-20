package net.robowiki.knn.implementations;

import ags.utils.dataStructures.MaxHeap;
import ags.utils.dataStructures.trees.thirdGenKD.DistanceFunction;
import ags.utils.dataStructures.trees.thirdGenKD.KdTree;
import ags.utils.dataStructures.trees.thirdGenKD.SquareEuclideanDistanceFunction;
import net.robowiki.knn.util.KNNPoint;

public class Rednaxela3rdGenTreeKNNSearch extends KNNImplementation {
    private KdTree<String> tree;
    private DistanceFunction dist;

    public Rednaxela3rdGenTreeKNNSearch(int dimension) {
        super(dimension);
        tree = new KdTree<String>(dimension);
        dist = new SquareEuclideanDistanceFunction();
    }

    @Override
    public void addPoint(double[] location, String value) {
        tree.addPoint(location, value);
    }

    @Override
    public KNNPoint[] getNearestNeighbors(double[] location, int size) {
        MaxHeap<String> entries = tree.findNearestNeighbors(location, size, dist);
        KNNPoint[] points = new KNNPoint[entries.size()];
        int i = entries.size() - 1;
        while (i >= 0) {
            String str = entries.getMax();
            double dist = entries.getMaxKey();
            entries.removeMax();
            points[i] = new KNNPoint(str, dist);
            i--;
        }
        return points;
    }
    
    @Override
    public String getName() {
        return "Rednaxela's kd-tree (3rd gen)";
    }
}
