package net.robowiki.knn.util;

/**
 * @author Nat Pavasant
 */
public class Comparator<V> implements Comparable<Comparator<V>> {
	private final V data;
	private final double value;

	public Comparator(V data, double value) {
		super();
		this.data = data;
		this.value = value;
	}

	public V getData() {
		return data;
	}

	public double getValue() {
		return value;
	}

	public int compareTo(Comparator<V> o) {
		return (int) Math.signum(value - o.value);
	}
}
