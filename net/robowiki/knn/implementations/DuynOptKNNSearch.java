package net.robowiki.knn.implementations;

import duyn.algorithm.nearestneighbours.Exemplar;
import duyn.algorithm.nearestneighbours.OptKdTree;
import duyn.algorithm.nearestneighbours.PrioNode;
import net.robowiki.knn.util.KNNPoint;

import java.util.LinkedList;
import java.util.List;

public class DuynOptKNNSearch extends KNNImplementation {
  final OptKdTree<StringExemplar> tree;
  public DuynOptKNNSearch(final int dimension) {
    super(dimension);
    tree = new OptKdTree<StringExemplar>();
  }

  @Override public void
  addPoint(final double[] location, final String value) {
    tree.add(new StringExemplar(location, value));
  }

  @Override public String
  getName() {
    return "duyn's optimised kd-tree";
  }

  @Override
  public KNNPoint[] getNearestNeighbors(final double[] location, final int size) {
    final List<KNNPoint> justPoints = new LinkedList<KNNPoint>();
    for(PrioNode<StringExemplar> sr : tree.search(location, size)) {
      final double distance = sr.priority;
      final StringExemplar pt = sr.data;
      justPoints.add(new KNNPoint(pt.value, distance));
    }
    final KNNPoint[] retVal = new KNNPoint[justPoints.size()];
    return justPoints.toArray(retVal);
  }

  class StringExemplar extends Exemplar {
    public final String value;
    public StringExemplar(final double[] coords, final String value) {
      super(coords);
      this.value = value;
    }
  }
}