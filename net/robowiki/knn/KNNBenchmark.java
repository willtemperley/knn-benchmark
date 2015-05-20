package net.robowiki.knn;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

import net.robowiki.knn.data.*;
import net.robowiki.knn.implementations.*;
import net.robowiki.knn.util.*;

/**
 * @author Nat Pavasant & Alex Schultz
 */
public class KNNBenchmark {
    public static Class<?>[] allSearchAlgorithms = new Class<?>[] {
            FlatKNNSearch.class,
            SimontonTreeKNNSearch.class,
            VoidiousTreeKNNSearch.class,
            Rednaxela2ndGenTreeKNNSearch.class,
            Rednaxela3rdGenTreeKNNSearch.class,
            Rednaxela3rdGenIteratedTreeKNNSearch.class,
            ChaseSanTreeKNNSearch.class,
            DuynBasicKNNSearch.class,
            DuynOptKNNSearch.class,
            DuynFastKNNSearch.class,
    };
    private TestResult[] results;

    public KNNBenchmark(Class<?>[] searchAlgorithms, int dimension, int numNeighbours, SampleData[] samples, int numReps, boolean warmupJIT) {
        if (warmupJIT) {
            // Warm up the JIT
            System.out.println("Warming up the JIT with 5 repetitions first...");
            PrintStream oldout = System.out;
            System.setOut(new PrintStream(new PipedOutputStream()));
            for (int i = 0; i < numReps; i++) {
                for (int j = 0; j < searchAlgorithms.length; j++) {
                    KNNTest test = new KNNTest(searchAlgorithms[j], dimension, numNeighbours, samples);
                    test.run();
                    System.gc();
                }
            }
            System.setOut(oldout);
        }

        long totalTime = -System.nanoTime();

        results = new TestResult[searchAlgorithms.length];

        DecimalFormat progress_df = new DecimalFormat("0.0#%");
        System.out.println("\nRunning tests...");
        String[][] solution = null;
        System.out.print("Progress "+progress_df.format(0)+" (/"+numReps+")");
        for (int i = 0; i < numReps; i++) {
            for (int j = 0; j < searchAlgorithms.length; j++) {
                KNNTest test = new KNNTest(searchAlgorithms[j], dimension, numNeighbours, samples);
                TestResult result;
                if (warmupJIT) {
                    result = test.run();
                } else {
                    result = test.forkedRun();
                }
                if (solution == null)
                    solution = result.getSearchResults();
                results[j] = TestResult.collectResults(results[j], result);
                System.gc();
                double progress = ((double)(i*searchAlgorithms.length+j+1))/(numReps*searchAlgorithms.length);
                System.out.print("\rProgress "+progress_df.format(progress)+" (repetition "+(i+1)+"/"+numReps+", candidate "+(j+1)+"/"+searchAlgorithms.length+")  ");
            }
        }
        System.out.println();
        System.out.println(" COMPLETED.\n");

        for (int i = 0; i < searchAlgorithms.length; i++) {
            results[i].checkAnswer(solution);
            System.out.println(results[i]);
        }

        Arrays.sort(results);
        System.out.println();
        StringBuilder sb = new StringBuilder();
        sb.append("BEST RESULT: \n");
        int z = 1;
        DecimalFormat time_df = new DecimalFormat("0.0000");
        for (TestResult tr : results) {
            sb.append(" - #").append(z++).append(" ").append(tr.getAlgorithmName())
                    .append(" [").append(time_df.format(tr.getAverageSearchTime() * 1E3))
                    .append("]\n");
        }
        System.out.println(sb.toString());

        totalTime += System.nanoTime();
        System.out.printf("Benchmark running time: %.2f seconds\n",
                totalTime / 1E9);
    }

    public void writeCSV(String filename) {
        try {
            FileWriter writer = new FileWriter(filename);

            double[][] searchTimes = new double[results.length][];
            for (int i = 0; i < results.length; i++) {
                searchTimes[i] = results[i].getSearchTimes();
                writer.append("\"").append(results[i].getAlgorithmName().replaceAll("\"", "\"\"")).append("\",");
            }
            writer.append("\n");

            int length = searchTimes[0].length;
            int gran = length/200;
            for (int i = 0; i < length/gran; i++) {
                for (double[] list : searchTimes) {
                    double d = 0;
                    int div = 0;
                    for (int j = 0; j < gran && gran*i+j < length; j++) {
                        d += list[gran*i+j];
                        div++;
                    }
                    d *= 1E3 / div;
                    writer.append(Double.toString(d)).append(",");
                }
                writer.append("\n");
            }
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static KNNImplementation createAlgorithm(Class<?> c, int dimensions) {
        if (c.getSuperclass().equals(KNNImplementation.class)) {
            KNNImplementation i;
            try {
                i = (KNNImplementation) c.getConstructors()[0].newInstance(dimensions);
            } catch (Exception e) {
                return null;
            }
            return i;
        }
        return null;
    }

    public static void main(String[] args) throws IOException {
        int numReps = 10, numNeighbours = 40, dimensions = 13;
        boolean jitWarmup = false;
        Class<?>[] selectedSearchAlgorithms = allSearchAlgorithms;
        /*Class<?>[] selectedSearchAlgorithms = new Class<?>[] {
            FlatKNNSearch.class,
            SimontonTreeKNNSearch.class,
            VoidiousTreeKNNSearch.class,
            Rednaxela2ndGenTreeKNNSearch.class,
            Rednaxela3rdGenTreeKNNSearch.class,
            Rednaxela3rdGenIteratedTreeKNNSearch.class,
            ChaseSanTreeKNNSearch.class,
            DuynBasicKNNSearch.class,
            DuynOptKNNSearch.class,
            DuynFastKNNSearch.class,
        };*/
        String filename = null;
        String outputfile = null;
        SampleData[] samples = null;

        System.out.println("K-NEAREST NEIGHBOURS ALGORITHMS BENCHMARK");
        System.out.println("-----------------------------------------");

        if (args.length == 0) {
            System.out.println("Starting in interactive mode...");
            UserInputManager ui = new UserInputManager(System.out, System.in);
            if (ui.getBoolean("Do you wish to use a data file?", true)) {
                if (ui.getBoolean("Do you wish to download a standard data file (Diamond vs CunobelinDC gun data) and use that?", true)) {
                    String downloadUrl = "http://homepages.ucalgary.ca/~agschult/robocode/gun-data-Diamond-vs-jk.mini.CunobelinDC%200.3.csv.gz";
                    String downloadName = "gun-data-Diamond-vs-jk.mini.CunobelinDC 0.3.csv.gz";
                    boolean success = FileDownloader.downloadFile(downloadUrl, downloadName);
                    if (!success) {
                        System.out.println("Failed to download file...");
                        System.exit(1);
                    }
                    filename = downloadName;
                } else {
                    filename = ui.getString("What data file do you wish to use?");
                }
            } else {
                System.out.println("Random data points will be generated.");
                dimensions = ui.getInteger("How many dimensions do you wish to test with?", dimensions);
                int points = ui.getInteger("How many random data and search points do you wish to have in the data set?", 10000);
                samples = RandomGenerator.generateRandomData(dimensions, points);
            }
            numNeighbours = ui.getInteger("How many neighbours should each search try to find?", numNeighbours);
            jitWarmup = !ui.getBoolean("Do you wish to run each test in an isolated process? This allows measurements to include the effect of the Just-In-Time compiler. Otherwise, Just-In-Time compiler be warmed up before running tests.", !jitWarmup);
            numReps = ui.getInteger("How many test repetitions should be performed?", numReps);
            if (ui.getBoolean("Do you wish to output timing information to a comma-seperated-value file for graphing?")) {
                outputfile = ui.getString("What filename should be written to?");
            }
            ui.close();
        } else {
            if (args.length < 3) {
                System.out.println("Usage:\n" +
                        "\tjava -jar net.robowiki.knn.KNNBenchmark neighbours repetitions datafile [outputfile]\n" +
                        "If you lack a data file, download one from:\n" +
                        "\thttp://homepages.ucalgary.ca/~agschult/robocode/gun-data-Diamond-vs-jk.mini.CunobelinDC%200.3.csv.gz\n" +
                        "'outputfile' is optional and is a file to output comma-seperated data for graphing to.");
                System.exit(1);
            }
            numNeighbours = Integer.parseInt(args[0]);
            numReps = Integer.parseInt(args[1]);
            filename = args[2];
            if (args.length > 3) {
                outputfile = args[3];
            }
        }

        // Read samples from file if necessary
        if (filename != null) {
            samples = CsvReader.readFile(filename);
            if (samples == null || samples.length == 0) {
                System.out.println("No data samples exist...");
                System.exit(1);
            }
            dimensions = samples[0].data.length;
        }

        if (samples == null) System.exit(1);

        System.out.printf(
                "Running %d repetition(s) for k-nearest neighbours searching:\n",
                numReps);
        System.out.printf(
                ":: %d dimension(s); %d neighbour(s)\n",
                dimensions, numNeighbours);

        KNNBenchmark benchmark = new KNNBenchmark(selectedSearchAlgorithms, dimensions, numNeighbours, samples, numReps, jitWarmup);
        if (outputfile != null) {
            System.out.println("Writing timing data to "+outputfile);
            benchmark.writeCSV(outputfile);
        }
        System.exit(0);
    }
}
