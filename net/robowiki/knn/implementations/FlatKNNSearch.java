package net.robowiki.knn.implementations;

import java.util.ArrayList;
import java.util.HashMap;

import voidious.utils.KdBucketTree;
import net.robowiki.knn.util.KNNPoint;

/**
 * @author Patrick Cupka & Alex Schultz
 */
public class FlatKNNSearch extends KNNImplementation {
    public FlatKNNSearch(int dimension) {
		super(dimension);
	}

	ArrayList<double[]> testArray = new ArrayList<double[]>();
    HashMap<double[], String> data = new HashMap<double[], String>();
	
	@Override
	public void addPoint(double[] location, String value) {
       testArray.add(location);
       data.put(location, value);
	}

	@Override
	public String getName() {
		return "Voidious' Linear search";
	}

	@Override
	public KNNPoint[] getNearestNeighbors(double[] searchPoint, int numNeighbors) {
		
		double[][] bruteClosestPoints = new double[numNeighbors][searchPoint.length];
		double[] nearestDistancesSq = new double[numNeighbors];
		for (int y = 0; y < numNeighbors; y++) {
			bruteClosestPoints[y] = testArray.get(y);
			nearestDistancesSq[y] = distanceSq(
					bruteClosestPoints[y], searchPoint);
		}
		double bruteClosestDistanceSqThreshold = findLongestDistanceSq(
				bruteClosestPoints, searchPoint);
		for (int y = numNeighbors; y < testArray.size(); y++) {
			double[] point = testArray.get(y);
			double thisDistanceSq = KdBucketTree.distanceSq(searchPoint,
					point);
			if (thisDistanceSq < bruteClosestDistanceSqThreshold) {
				bruteClosestDistanceSqThreshold = findAndReplaceLongestDistanceSq(
						bruteClosestPoints, nearestDistancesSq, point,
						thisDistanceSq);
			}
		}
		
		KNNPoint[] arr = new KNNPoint[numNeighbors];
		
		for (int i = 0; i < numNeighbors; i++) {
			arr[i] = new KNNPoint(data.get(bruteClosestPoints[i]), nearestDistancesSq[i]); 
		}
		
		return arr;
	}

	public static double distanceSq(double[] p1, double[] p2) {

		double sum = 0;
		for (int x = 0; x < p1.length; x++) {
			double z = (p1[x] - p2[x]);
			sum += z * z;
		}

		return sum;
	}

	public static double findLongestDistanceSq(double[][] points,
			double[] testPoint) {

		double longestDistanceSq = 0;
		for (int x = 0; x < points.length; x++) {
			double distanceSq = KdBucketTree.distanceSq(points[x], testPoint);
			if (distanceSq > longestDistanceSq) {
				longestDistanceSq = distanceSq;
			}
		}

		return longestDistanceSq;
	}

	public static double findLongestDistanceSq(double[] nearestDistancesSq) {

		double longestDistanceSq = Double.NEGATIVE_INFINITY;
		for (int x = 0; x < nearestDistancesSq.length; x++) {
			double distanceSq = nearestDistancesSq[x];
			if (distanceSq > longestDistanceSq) {
				longestDistanceSq = distanceSq;
			}
		}

		return longestDistanceSq;
	}

	public static double findAndReplaceLongestDistanceSq(double[][] points,
			double[] nearestDistances, double[] newPoint,
			double newPointDistanceSq) {

		double longestDistanceSq = 0;
		double newLongestDistanceSq = 0;
		int longestIndex = 0;
		for (int x = 0; x < points.length; x++) {
			double distanceSq = nearestDistances[x];
			if (distanceSq > longestDistanceSq) {
				newLongestDistanceSq = longestDistanceSq;
				longestDistanceSq = distanceSq;
				longestIndex = x;
			} else if (distanceSq > newLongestDistanceSq) {
				newLongestDistanceSq = distanceSq;
			}
		}
		points[longestIndex] = newPoint;
		nearestDistances[longestIndex] = newPointDistanceSq;

		return Math.max(newLongestDistanceSq, newPointDistanceSq);
	}
}
