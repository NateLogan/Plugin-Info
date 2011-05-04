package org.petricek.bukkit.plugininfo.integration;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author Michal Petříček
 */
public class FileManager {

    public void save(String file, InputStream in) {
        FileOutputStream fos = null;
        BufferedOutputStream out = null;

        try {
            fos = new FileOutputStream(file);
            out = new BufferedOutputStream(fos);
        } catch (FileNotFoundException ex) {
        }

        try {
            while (in.available() > 0) {
                out.write(in.read());
            }
            out.flush();
        } catch (IOException ex) {
        }
    }

    public static boolean fileExists(String file) {
        return (new File(file)).exists();
    }
}
