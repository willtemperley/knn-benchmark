package net.robowiki.knn.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

/**
 * @author Alex Schultz
 */
public class CsvReader {
	public static SampleData[] readFile(String filename) {
		File file = new File(filename);

		System.out.println("Reading data from " + file.getName());
		int saveCount = 0, searchCount = 0;

		BufferedReader bufRdr;
		ArrayList<SampleData> data = new ArrayList<SampleData>();
		
		boolean error = false;
		
		try {
			if (filename.endsWith(".gz")) {
				GZIPInputStream gstream = new GZIPInputStream(new FileInputStream(file));
				bufRdr = new BufferedReader(new InputStreamReader(gstream));
			}
			else {
				bufRdr = new BufferedReader(new FileReader(file));
			}
			String line = null;

			//read each line of the csv file
			while((line = bufRdr.readLine()) != null)
			{
				if (line.length() == 0) {
					continue;
				}
				String[] strings = line.split(",");
				boolean save = false, search = false;
				if (strings[0].contains("save")) {
					save = true;
					saveCount++;
				}
				if (strings[0].contains("search")) {
					search = true;
					searchCount++;
				}
				double[] location = new double[strings.length - 1];
				for (int j = 0; j < location.length; j++) {
					location[j] = Double.parseDouble(strings[j+1]);
				}
				data.add(new SampleData(save, search, location));
			}

			//close the file
			bufRdr.close();
		} catch (FileNotFoundException e) {
			System.err.println("Input file not found.");
			error = true;
		} catch (IOException e) {
			e.printStackTrace();
			error = true;
		}
		
		if (error) {
			System.err.println("ERROR: Cannot read input file.");
			return null;
		}

		System.out.printf(
				"Read %d saves and %d searches.\n\n",
				saveCount, searchCount);

		return data.toArray(new SampleData[data.size()]);
	}
}