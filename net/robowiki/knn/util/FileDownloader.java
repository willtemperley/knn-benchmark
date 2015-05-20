package net.robowiki.knn.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 *
 */
public class FileDownloader {
    public static boolean downloadFile(String urlString, String filename) {
        System.out.print("Downloading \""+filename+"\"...");
        InputStream is = null;
        OutputStream file = null;
        try {
            URL url  = new URL(urlString);
            is = url.openConnection().getInputStream();
            file = new FileOutputStream(filename);
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = is.read(buf)) != -1) {
                file.write(buf, 0, bytesRead);
            }
            System.out.println(" Download complete.");
        }
        catch (MalformedURLException e) {
            System.err.println(e.toString());
            return false;
        }
        catch ( IOException e) {
            System.err.println(e.toString());
            return false;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    /// Nothing can be done about that...
                }
            }
            if (file != null) {
                try {
                    file.close();
                } catch (IOException e) {
                    // Nothing can be done about that...
                }
            }
        }
        return true;
    }
}
