/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.petricek.bukkit.plugininfo;

import java.io.File;
import org.petricek.bukkit.plugininfo.util.BetterConfig;

/**
 *
 * @author Michal Petříček
 */
public class PluginSettings {

    private static final String settingsFile = "settings.yml";
    private static BetterConfig config;
    public static int entriesPerPage;
    public static String outputFolder;
    
    public static String xmlFileName;
    public static String xmlOutputFolder;
    public static boolean xmlOnEnableSave;

    public static String htmlFileName;
    public static String htmlOutputFolder;
    public static boolean htmlOnEnableSave;

    public static String txtFileName;
    public static String txtOutputFolder;
    public static boolean txtOnEnableSave;

    public static void initialize(File dataFolder) {
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        File configFile = new File(dataFolder, settingsFile);
        config = new BetterConfig(configFile);
        config.load();

        entriesPerPage = config.getInt("entriesPerPage", 9);
        outputFolder = config.getString("outputFolder", dataFolder.getPath());

        xmlFileName = config.getString("xmlFileName", "plugins.xml");
        xmlOutputFolder = config.getString("xmlOutputFolder", "");
        xmlOnEnableSave = config.getBoolean("xmlOnEnableSave", false);

        htmlFileName = config.getString("htmlFileName", "plugins.html");
        htmlOutputFolder = config.getString("htmlOutputFolder", "");
        htmlOnEnableSave = config.getBoolean("htmlOnEnableSave", false);

        txtFileName = config.getString("txtFileName", "plugins.txt");
        txtOutputFolder = config.getString("txtOutputFolder", "");
        txtOnEnableSave = config.getBoolean("txtOnEnableSave", false);

        save();
    }

    public static boolean save(){
        return config.save();
    }
}
