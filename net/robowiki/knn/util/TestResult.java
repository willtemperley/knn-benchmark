package net.robowiki.knn.util;

import net.robowiki.knn.util.KNNPoint;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 
 */
public class TestResult implements Comparable<TestResult>, Cloneable, Serializable {
    // Cumulative
    private long searchTime;
    private int searchCount;
    private long worstSearchTime;
    private long addTime;
    private int addCount;
    private int repCount;
    private ArrayList<Long> searchTimes;
    // Non-cumulative
    private final int dataSize;
    private final int numNeighbours;
    private final String algorithm;
    private Double accuracy;
    private ArrayList<String[]> results;

    public static TestResult collectResults(TestResult accumulator, TestResult newResult) {
        if (accumulator == null) {
            try {
                return (TestResult) newResult.clone();
            } catch (CloneNotSupportedException e) {
                return null; // Shouldn't happens
            }
        }
        accumulator.accumulateTimes(newResult);
        return accumulator;
    }

    public TestResult(String algorithm, int dataSize, int numNeighbours) {
        this.algorithm = algorithm;
        this.results = new ArrayList<String[]>();
        this.searchTimes = new ArrayList<Long>();
        this.dataSize = dataSize;
        this.numNeighbours = numNeighbours;
        repCount = 1;
        accuracy = null;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        TestResult result = (TestResult) super.clone();
        result.searchTimes = new ArrayList<Long>(searchTimes);
        result.results = new ArrayList<String[]>(results);
        return result;
    }

    public void recordSearch(KNNPoint[] result, long time) {
        String[] strings = new String[result.length];
        for (int i = 0; i < result.length; i++) {
            strings[i] = result[i].getValue().intern();
        }
        results.add(strings);
        searchTimes.add(time);
        searchCount++;
        searchTime += time;
        worstSearchTime = Math.max(time, worstSearchTime);
    }

    public void recordAdd(long time) {
        addCount++;
        addTime += time;
    }

    public void checkAnswer(String[][] solution) {
        if (solution == null) {
            accuracy = 1d;
            return;
        }

        int total = 0;
        int count = 0;
        for (int i = 0; i < solution.length; i++) {
            String[] thisSolution = solution[i];
            total += thisSolution.length;
            if (i >= results.size()) {
                continue;
            }

            String[] thisResult = results.get(i);
            List<String> abc = Arrays.asList(thisSolution);

            for (String s : thisResult) {
                if (abc.contains(s))
                    count++;
            }
        }

        accuracy = ((double) count / total);
    }


    private void accumulateTimes(TestResult other) {
        for (int i = 0; i < searchTimes.size(); i++) {
            this.searchTimes.set(i, this.searchTimes.get(i) + other.searchTimes.get(i));
        }
        this.searchTime += other.searchTime;
        this.searchCount += other.searchCount;
        this.worstSearchTime += other.worstSearchTime;
        this.addTime += other.addTime;
        this.addCount += other.addCount;
        this.repCount += other.repCount;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        DecimalFormat time_df = new DecimalFormat("0.0000");
        DecimalFormat percent_df = new DecimalFormat("0.0#%");

        sb.append("RESULT << k-nearest neighbours search with ").append(
                getAlgorithmName()).append(" >>").append("\n");
        sb.append(": Average searching time       = ").append(
                time_df.format(getAverageSearchTime() * 1E3)).append(" miliseconds\n");
        sb.append(": Average worst searching time = ").append(
                time_df.format(getAverageWorstSearchTime() * 1E3)).append(" miliseconds\n");
        sb.append(": Average adding time          = ").append(
                time_df.format(getAverageAddTime() * 1E6)).append(" microseconds\n");
        sb.append(": Accuracy                     = ").append(
                percent_df.format(getAccuracy())).append("\n");

        return sb.toString();
    }

    public int compareTo(TestResult arg0) {
        return Double.compare(getAverageSearchTime(), arg0.getAverageSearchTime());
    }

    public double getAverageSearchTime() {
        return (((double)searchTime) / searchCount)/1E9;
    }

    public double getAverageWorstSearchTime() {
        return (((double)worstSearchTime) / repCount)/1E9;
    }

    public double getAverageAddTime() {
        return (((double)addTime) / addCount)/1E9;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public String getAlgorithmName() {
        return algorithm;
    }

    public String[][] getSearchResults() {
        return results.toArray(new String[results.size()][]);
    }

    public double[] getSearchTimes() {
        double[] searchTimesArray = new double[searchTimes.size()];
        for (int i = 0; i < searchTimesArray.length; i++) {
            searchTimesArray[i] = (((double)searchTimes.get(i))/repCount)/1E9;
        }
        return searchTimesArray;
    }
}
