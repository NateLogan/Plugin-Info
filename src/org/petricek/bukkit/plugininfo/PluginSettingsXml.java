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
public class PluginSettingsXml {

    private static final String settingsFile = "settings_xml.yml";
    private static BetterConfig config;
    public static boolean printCommands;
    public static boolean printCommandsDetails;
    public static boolean printDepend;
    public static boolean printAuthors;
    public static boolean printDatabaseEnabled;
    public static boolean printWeb;
    public static boolean printDesc;
    public static boolean printFullname;
    public static boolean printVersion;
    public static boolean printStats;
    public static boolean printPluginEnabled;
    public static boolean printPlugins;
    public static boolean printGeneratedTime;
    public static boolean printServerInfo;
    public static boolean printMinecraftServerInfo;
    public static boolean printBukkitInfo;

    public static void initialize(File dataFolder) {
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        File configFile = new File(dataFolder, settingsFile);
        config = new BetterConfig(configFile);
        config.load();

        printCommands = config.getBoolean("printCommands", false);
        printCommandsDetails = config.getBoolean("printCommandsDetails", false);
        printDepend = config.getBoolean("printDepend", true);
        printAuthors = config.getBoolean("printAuthors", true);
        printDatabaseEnabled = config.getBoolean("printDatabaseEnabled", true);
        printWeb = config.getBoolean("printWeb", true);
        printDesc = config.getBoolean("printDesc", true);
        printFullname = config.getBoolean("printFullname", false);
        printVersion = config.getBoolean("printVersion", true);
        printStats = config.getBoolean("printStats", true);
        printPluginEnabled = config.getBoolean("printPluginEnabled", false);
        printPlugins = config.getBoolean("printPlugins", true);
        printGeneratedTime = config.getBoolean("printGeneratedTime", true);
        printServerInfo = config.getBoolean("printServerInfo", true);
        printMinecraftServerInfo = config.getBoolean("printMinecraftServerInfo", true);
        printBukkitInfo = config.getBoolean("printBukkitInfo", true);

        save();
    }

    public static boolean save(){
        return config.save();
    }
}
