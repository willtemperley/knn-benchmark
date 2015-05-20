package net.robowiki.knn.util;

import java.io.*;

/**
 * @author Alex Schultz
 */
public class UserInputManager {
    private PrintStream out;
    private BufferedReader reader;

    public UserInputManager(PrintStream out, InputStream in) {
        this.out = out;
        this.reader = new BufferedReader(new InputStreamReader(in));
    }

    public void close() throws IOException {
        reader.close();
    }

    public String getString(String question) throws IOException {
        out.println(question);
        out.print("> ");
        out.flush();
        return reader.readLine().trim();
    }

    public int getInteger(String question) throws IOException {
        return getInteger(question, null);
    }

    public int getInteger(String question, Integer default_value) throws IOException {
        if (default_value == null) {
            question += " ["+default_value+"]";
        }
        while (true) {
            String input = getString(question);
            if (input.length() == 0 && default_value != null) {
                return default_value;
            }
            int value;
            try {
                value = Integer.parseInt(input);
            } catch (NumberFormatException ex) {
                System.out.println("Please enter an integer.");
                continue;
            }
            return value;
        }
    }

    public boolean getBoolean(String question) throws IOException {
        return getBoolean(question, null);
    }

    public boolean getBoolean(String question, Boolean default_value) throws IOException {
        question += " (y/n)";
        if (default_value != null) {
            question += " [" + ((default_value) ? "y" : "n")+ "]";
        }
        while (true) {
            String input = getString(question).toLowerCase();
            if (input.length() == 0 && default_value != null) {
                return default_value;
            }
            if (input.equals("y")) {
                return true;
            } else if (input.equals("n")) {
                return false;
            } else {
                System.out.println("Please enter 'y' or 'n'.");
            }
        }
    }
}
