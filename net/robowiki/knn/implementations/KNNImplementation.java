package net.robowiki.knn.implementations;

import nat.tree.M;
import net.robowiki.knn.util.KNNEntry;
import net.robowiki.knn.util.KNNPoint;

/**
 * @author Nat Pavasant
 */
public abstract class KNNImplementation {
	protected final int dimension;

	public KNNImplementation(int dimension) {
		this.dimension = dimension;
	}

	public abstract void addPoint(double[] location, String value);
	public abstract KNNPoint[] getNearestNeighbors(double[] location, int size);
	public abstract String getName();

	public final long addDataPoint(KNNEntry e) {
		long time = -System.nanoTime();
		addPoint(e.getLocation(), e.getValue());
		return time += System.nanoTime();
	}

	public final Neighbors getNeighbors(double[] location, int size) {
		long time = -System.nanoTime();
		KNNPoint[] result = getNearestNeighbors(location, size);
		return new Neighbors(result, time += System.nanoTime());
	}
	
	protected final double getDistance(double[] p1, double[] p2) {
		double result = 0;
		for (int i = 0; i < p1.length; i++) {
			result += M.sqr(p1[i] - p2[i]);
		}
		//return M.sqrt(results);
		return result;
	}

	public static final class Neighbors {
		public final long time;
		public final KNNPoint[] result;

		public Neighbors(KNNPoint[] result, long time) {
			this.time = time;
			this.result = result;
		}

		public long getTime() {
			return time;
		}

		public KNNPoint[] getResult() {
			return result;
		}
	}
}
