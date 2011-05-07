package org.petricek.bukkit.plugininfo.integration;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.petricek.bukkit.plugininfo.BukkitLogger;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;
import org.petricek.bukkit.plugininfo.PluginInfo;
import org.w3c.dom.Document;
import org.w3c.dom.ProcessingInstruction;

/**
 *
 * @author Michal Petříček
 */
public class FileManager {

    public static boolean save(String file, String text) {
        return save(file, new ByteArrayInputStream(text.getBytes()));
    }
    
    public static boolean save(String file, InputStream in) {
        File f = new File(file);
        BukkitLogger.info("Saving data into file: " + f.getAbsolutePath());
        try {
            FileOutputStream fos = null;
            BufferedOutputStream out = null;

            try {
                if (f.exists()) {
                    f.delete();
                    f.createNewFile();
                } else {
                    f.createNewFile();
                }
            } catch (IOException ex) {
                BukkitLogger.warning("Could not create file " + file + ": " + ex.getMessage());
            }

            try {
                fos = new FileOutputStream(file);
                out = new BufferedOutputStream(fos);
            } catch (FileNotFoundException ex) {
                BukkitLogger.warning("File not found (" + file + "): " + ex.getMessage());
            }

            try {
                while (in.available() > 0) {
                    out.write(in.read());
                }
                out.flush();
                return true;
            } catch (IOException ex) {
                BukkitLogger.warning("File writing error (" + file + "): " + ex.getMessage());
            }
        } catch (Exception ex) {
            BukkitLogger.warning("Unknown Exception while saving file " + file + ": " + ex.getMessage());
        }
        return false;
    }

    public static boolean saveXml(String file, Document doc) {
        if (doc == null) {
            return false;
        }
        File f = new File(file);
        BukkitLogger.info("Saving data into file: " + f.getAbsolutePath());
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            //doc.setXmlStandalone(true);
           
            if(PluginInfo.settingsXml.xslt != null && !PluginInfo.settingsXml.xslt.isEmpty()){
                ProcessingInstruction pi = doc.createProcessingInstruction("xml-stylesheet", "type=\"text/xsl\" href=\"" + PluginInfo.settingsXml.xslt + "\"");
                doc.insertBefore(pi, doc.getDocumentElement());
            }

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(f);
            transformer.transform(source, result);
            return true;
        } catch (TransformerFactoryConfigurationError ex) {
            BukkitLogger.warning("Transform Configuration Exception while saving XML file " + file + ": " + ex.getMessage());
        } catch (TransformerException ex) {
            BukkitLogger.warning("Transform Exception while saving XML file " + file + ": " + ex.getMessage());
        } catch (Exception ex) {
            BukkitLogger.warning("Unknown Exception while saving XML file " + file + ": " + ex.getMessage());
        }
        return false;
    }

    public static InputStream load(String file) {
        FileInputStream fis = null;
        BufferedInputStream in = null;

        try {
            fis = new FileInputStream(file);
            in = new BufferedInputStream(fis);
        } catch (FileNotFoundException ex) {
            BukkitLogger.warning("Could not read file " + file + ": " + ex.getMessage());
        }

        return in;
    }

    public static String loadAsString(String file) {
        BufferedInputStream in = (BufferedInputStream) load(file);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder string = new StringBuilder();
        try {
            String s = null;
            while ((s = reader.readLine()) != null) {
                string.append(s);
            }
        } catch (IOException ex) {
            BukkitLogger.warning("Could not read file " + file + ": " + ex.getMessage());
        }
        return string.toString();
    }

    public static boolean fileExists(String file) {
        return (new File(file)).exists();
    }
}
