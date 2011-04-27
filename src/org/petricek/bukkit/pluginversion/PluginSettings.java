/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.petricek.bukkit.pluginversion;

import java.io.File;
import me.taylorkelly.help.BetterConfig;

/**
 *
 * @author Michal Petříček
 */
public class PluginSettings {

    private static final String settingsFile = "settings.yml";
    public static int entriesPerPage;

    public static void initialize(File dataFolder) {
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        File configFile = new File(dataFolder, settingsFile);
        BetterConfig config = new BetterConfig(configFile);
        config.load();
        entriesPerPage = config.getInt("entriesPerPage", 9);
        config.save();
    }
}
