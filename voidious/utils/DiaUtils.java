package voidious.utils;

/**
 * Code is open source, released under the RoboWiki Public Code License:
 * http://robowiki.net/?RWPCL
 */

public class DiaUtils {

	public static double round(double d, int i) {
		long powerTen = 1;

		for (int x = 0; x < i; x++) {
			powerTen *= 10;
		}

		return ((double) Math.round(d * powerTen)) / powerTen;
	}

}
