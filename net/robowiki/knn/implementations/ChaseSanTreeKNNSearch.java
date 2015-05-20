package net.robowiki.knn.implementations;

import net.robowiki.knn.util.KNNPoint;
import org.csdgn.util.kd2.KDTreeC;

/**
 *
 */
public class ChaseSanTreeKNNSearch extends KNNImplementation {
    KDTreeC tree;

    public ChaseSanTreeKNNSearch(int dimension) {
        super(dimension);
        tree = new KDTreeC(dimension);
    }

    @Override
    public void addPoint(double[] location, String value) {
        tree.add(location, value);
    }

    @Override
    public KNNPoint[] getNearestNeighbors(double[] location, int size) {
        KDTreeC.Item[] items = tree.getNearestNeighbor(location, size);
        KNNPoint[] array = new KNNPoint[items.length];
        for (int i = 0; i < items.length; i++) {
            array[i] = new KNNPoint((String)items[i].obj, items[i].distance);
        }
        return array;
    }

    @Override
    public String getName() {
        return "Chase-san's kd-tree (KDTreeC)";
    }
}
