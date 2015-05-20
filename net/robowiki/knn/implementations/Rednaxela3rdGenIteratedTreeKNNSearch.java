package net.robowiki.knn.implementations;

import ags.utils.dataStructures.MaxHeap;
import ags.utils.dataStructures.trees.thirdGenKD.DistanceFunction;
import ags.utils.dataStructures.trees.thirdGenKD.KdTree;
import ags.utils.dataStructures.trees.thirdGenKD.NearestNeighborIterator;
import ags.utils.dataStructures.trees.thirdGenKD.SquareEuclideanDistanceFunction;
import net.robowiki.knn.util.KNNPoint;

import java.util.ArrayList;

public class Rednaxela3rdGenIteratedTreeKNNSearch extends KNNImplementation {
    private KdTree<String> tree;
    private DistanceFunction dist;

    public Rednaxela3rdGenIteratedTreeKNNSearch(int dimension) {
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
        NearestNeighborIterator<String> iter = tree.getNearestNeighborIterator(location, size, dist);
        KNNPoint[] points = new KNNPoint[size];
        int i = 0;
        while (i < size) {
            String str = iter.next();
            double dist = iter.distance();
            points[i] = new KNNPoint(str, dist);
            i++;
        }
        return points;
    }

    @Override
    public String getName() {
        return "Rednaxela's kd-tree (3rd gen, iterated)";
    }
}