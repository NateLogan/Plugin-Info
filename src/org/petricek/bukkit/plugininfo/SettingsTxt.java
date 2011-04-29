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
public class SettingsTxt {

    private final String settingsFile = "settings_txt.yml";
    private final File dataFolder;
    private BetterConfig config;

    public boolean printTimeStamp;
    public boolean printComments;
    public String commentsChar;
    public String delimiter;
   
    public boolean printServerInfo; 
    public boolean printMinecraftServerInfo;
    public boolean printBukkitInfo;

    public boolean printPluginInfo;
        
    public SettingsTxt(File dataFolder) {
        this.dataFolder = dataFolder;
        initialize();
    }

    public final void initialize() {
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        File configFile = new File(dataFolder, settingsFile);
        config = new BetterConfig(configFile);
        config.load();

        printTimeStamp = config.getBoolean("printTimeStamp", true);
        printComments = config.getBoolean("printComments", true);
        printServerInfo = config.getBoolean("printServerInfo", true);
        printMinecraftServerInfo = config.getBoolean("printMinecraftServerInfo", true);
        printBukkitInfo = config.getBoolean("printBukkitInfo", true);
        printPluginInfo = config.getBoolean("printPluginInfo", true);
        commentsChar = config.getString("commentsChar", "#");
        delimiter = config.getString("delimiter", " - ");

        save();
    }

    public boolean save(){
        return config.save();
    }
}
