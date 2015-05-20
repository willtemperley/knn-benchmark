package net.robowiki.knn.util;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @author Nat Pavasant
 */
public class KNNEntry implements Serializable {
	final String value;
	final double[] location;

	public KNNEntry(String value, double[] location) {
		super();
		this.value = value;
		this.location = location;
	}

	public String getValue() {
		return value;
	}

	public double[] getLocation() {
		return location;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(location);
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof KNNEntry))
			return false;
		KNNEntry other = (KNNEntry) obj;
		if (!Arrays.equals(location, other.location))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

}
