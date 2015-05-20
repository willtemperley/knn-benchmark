package net.robowiki.knn.data;

/**
 * @author Nat Pavasant & Alex Schultz
 */
public class RandomGenerator {
	public static SampleData[] generateRandomData(int dimensions, int points) {
		SampleData[] a = new SampleData[points*2];

		for (int i = 0; i < a.length; i++) {
			a[i] = new SampleData(true, false,
					generateRandomPoint(dimensions));
            i++;
            a[i] = new SampleData(false, true,
					generateRandomPoint(dimensions));
		}

		return a;
	}

	public static double[] generateRandomPoint(int dimension) {
		double[] p = new double[dimension];
		for (int j = 0; j < dimension; j++) {
			p[j] = Math.random();
		}
		return p;
	}
}
