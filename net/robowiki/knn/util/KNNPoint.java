package net.robowiki.knn.util;

import java.io.Serializable;

/**
 * @author Nat Pavasant
 */
public class KNNPoint implements Comparable<KNNPoint>, Serializable {
	final String value;
	final double distance;

	public KNNPoint(String value, double distance) {
		this.value = value;
		this.distance = distance;
	}

	public String getValue() {
		return value;
	}

	public double getDistance() {
		return distance;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(distance);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof KNNPoint))
			return false;
		KNNPoint other = (KNNPoint) obj;
		if (Double.doubleToLongBits(distance) != Double
				.doubleToLongBits(other.distance))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	public int compareTo(KNNPoint a) {
		return (int) Math.signum(distance - a.distance);
	}
}
