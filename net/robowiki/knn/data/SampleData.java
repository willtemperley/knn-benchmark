package net.robowiki.knn.data;

import java.io.Serializable;
import java.util.Random;

import net.robowiki.knn.util.KNNEntry;

/**
 * @author Nat Pavasant & Alex Schultz
 */
public class SampleData implements Serializable {
	public final boolean save, search;
	public final double[] data;
	public final KNNEntry entry;
	
    public SampleData(boolean save, boolean search, double[] data) {
    	this.save = save;
    	this.search = search;
    	this.data = data;
    	
    	if (this.save) {
    		entry = new KNNEntry(generateRandomString(), data);
    	}
    	else {
    		entry = null;
    	}
    }
    
	private static String generateRandomString() {
		String chars = "abcdefghijklmonpqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		Random r = new Random();
		char[] buf = new char[25];

		for (int i = 0; i < buf.length; i++) {
			buf[i] = chars.charAt(r.nextInt(chars.length()));
		}

		return new String(buf);
	}
}
