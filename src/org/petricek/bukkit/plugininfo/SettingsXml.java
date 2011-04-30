package org.petricek.bukkit.plugininfo;

import java.io.File;
import org.petricek.bukkit.plugininfo.util.BetterConfig;

/**
 *
 * @author Michal Petříček
 */
public class SettingsXml {

    private static final String SETTINGS_FILE = "settings_xml.yml";
    private final File dataFolder;

    private BetterConfig config;

    public String xslt;
    public boolean printCommands;
    public boolean printCommandsDetails;
    public boolean printDepend;
    public boolean printAuthors;
    public boolean printDatabaseEnabled;
    public boolean printWeb;
    public boolean printDesc;
    public boolean printFullname;
    public boolean printVersion;
    public boolean printStats;
    public boolean printPluginEnabled;
    public boolean printPlugins;
    public boolean printGeneratedTime;
    public boolean printServerInfo;
    public boolean printMinecraftServerInfo;
    public boolean printBukkitInfo;

    SettingsXml(File dataFolder) {
        this.dataFolder = dataFolder;
        initialize();
    }

    public final void initialize() {
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        File configFile = new File(dataFolder, SETTINGS_FILE);
        config = new BetterConfig(configFile);
        config.load();

        xslt = config.getString("xslt", "");
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

    public boolean save(){
        return config.save();
    }
}
