/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.petricek.bukkit.plugininfo.integration;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import org.petricek.bukkit.plugininfo.BukkitLogger;

/**
 *
 * @author Michal Petříček
 */
public class FtpManager {

    public static boolean upload(String ftpServer, String user, String password, String fileName, File source) {
        boolean success = true;
        if (ftpServer != null && fileName != null && source != null) {
            BukkitLogger.info("Uploading file \"" + source.getPath() + "\"");

            StringBuilder sb = new StringBuilder("ftp://");
            // check for authentication else assume its anonymous access.
            if (user != null && !user.isEmpty() && password != null && !password.isEmpty()) {
                sb.append(user);
                sb.append(':');
                sb.append(password);
                sb.append('@');
            }
            sb.append(ftpServer);
            sb.append('/');
            sb.append(fileName);
            /*
             * type ==> a=ASCII mode, i=image (binary) mode, d= file directory listing
             */
            sb.append(";type=i");

            BufferedInputStream bis = null;
            BufferedOutputStream bos = null;
            try {
                URL url = new URL(sb.toString());
                URLConnection urlc = url.openConnection();

                bos = new BufferedOutputStream(urlc.getOutputStream());
                bis = new BufferedInputStream(new FileInputStream(source));

                int i;
                // read byte by byte until end of stream
                while ((i = bis.read()) != -1) {
                    bos.write(i);
                }
            } catch (MalformedURLException ex) {
                BukkitLogger.warning("Unknown host");
                success = false;
            } catch (IOException ex) {
                BukkitLogger.warning("Could not upload the file: " + ex.getMessage());
                success = false;
            } catch (Exception ex) {
                BukkitLogger.severe("Unknown error", ex);
                success = false;
            } finally {
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (Exception ex) {
                    }
                }
                if (bos != null) {
                    try {
                        bos.close();
                    } catch (Exception ex) {
                    }
                }
            }
        } else {
            BukkitLogger.warning("Input not available.");
            success = false;
        }
        return success;
    }


}
