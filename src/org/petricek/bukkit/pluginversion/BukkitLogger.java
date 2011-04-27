/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.petricek.bukkit.pluginversion;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Michal Petříček
 */
public class BukkitLogger {

    public static final Logger log = Logger.getLogger("Minecraft");

    public static void severe(String string, Exception ex) {
        log.log(Level.SEVERE, "[PluginVersion] " + string, ex);

    }

    public static void severe(String string) {
        log.log(Level.SEVERE, "[PluginVersion] " + string);
    }

    public static void info(String string) {
        log.log(Level.INFO, "[PluginVersion] " + string);
    }

    public static void warning(String string) {
        log.log(Level.WARNING, "[PluginVersion] " + string);
    }
}