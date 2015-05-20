package net.robowiki.knn.util;

import net.robowiki.knn.KNNBenchmark;
import net.robowiki.knn.data.SampleData;
import net.robowiki.knn.implementations.KNNImplementation;
import sun.misc.IOUtils;

import java.io.*;

/**
 *
 */
public class KNNTest implements Serializable {
    private final Class<?> algorithmClass;
    private final int dimensions;
    private final int numNeighbours;
    private final SampleData[] data;

    public KNNTest(Class<?> algorithmClass, int dimensions, int numNeighbours, SampleData[] data) {
        this.data = data;
        this.algorithmClass = algorithmClass;
        this.dimensions = dimensions;
        this.numNeighbours = numNeighbours;
    }

    public TestResult run() {
        KNNImplementation algorithm = KNNBenchmark.createAlgorithm(algorithmClass, dimensions);
        TestResult result = new TestResult(algorithm.getName(), data.length, numNeighbours);

        int addCount = 0;
        for (SampleData sample : data) {
            if (sample.save) {
                long time = -System.nanoTime();
                algorithm.addDataPoint(sample.entry);
                time += System.nanoTime();
                result.recordAdd(time);
                addCount++;
            }

            if (sample.search) {
                long time = -System.nanoTime();
                KNNPoint[] neighbourList = algorithm.getNearestNeighbors(sample.data, Math.min(numNeighbours, addCount));
                time += System.nanoTime();
                result.recordSearch(neighbourList, time);
            }
        }

        return result;
    }

    private static Thread pipe(final InputStream src, final PrintStream dest) {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    byte[] buffer = new byte[1024];
                    for (int n = 0; n != -1; n = src.read(buffer)) {
                        dest.write(buffer, 0, n);
                    }
                } catch (IOException ignored) {}
            }
        });
        thread.start();
        return thread;
    }

    public TestResult forkedRun() {
        String javaHome = System.getProperty("java.home");
        String javaBin = javaHome +
                File.separator + "bin" +
                File.separator + "java";
        String classpath = System.getProperty("java.class.path");
        String className = this.getClass().getCanonicalName();

        ProcessBuilder builder = new ProcessBuilder(
                javaBin, "-cp", classpath, className);


        TestResult result;
        try {
            Process process = builder.start();
            Thread pipeThread = pipe(process.getErrorStream(), System.err);
            // Wait for ready signal
            ObjectInputStream input = new ObjectInputStream(process.getInputStream());
            input.readObject();
            // Serialize test
            ObjectOutputStream output = new ObjectOutputStream(process.getOutputStream());
            output.writeObject(this);
            output.flush();
            // Read result
            result = (TestResult) input.readObject();
            output.close();
            input.close();
            process.waitFor();
            pipeThread.join();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        // Wait for serialized KNNTest
        ObjectOutputStream output = new ObjectOutputStream(System.out);
        // Signal that it is ready for input
        output.writeObject(true);
        output.flush();
        ObjectInputStream input = new ObjectInputStream(System.in);
        // Read the KNNTest class
        KNNTest test = (KNNTest) input.readObject();
        // Run the test
        TestResult result = test.run();
        // Send the results to stdout
        output.writeObject(result);
        // Clean up
        output.close();
        input.close();
        System.exit(0);
    }
}
