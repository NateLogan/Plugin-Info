package org.petricek.bukkit.plugininfo.controller;

import org.bukkit.Server;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.petricek.bukkit.plugininfo.BukkitLogger;
import org.petricek.bukkit.plugininfo.PluginInfo;

/**
 *
 * @author Michal Petříček
 */
public class ApiCraftController {

    private ApiCraftListener listener = null;
    private boolean enabled = false;
    private boolean apiCraftDetected = false;
    private final Server server;
    private final JavaPlugin plugin;

    public ApiCraftController(Server server, JavaPlugin plugin) {
        this.server = server;
        this.plugin = plugin;

        init();
    }

    private void init() {
        if (PluginInfo.settings.enableApiCraft) {
            Plugin apiCraft = server.getPluginManager().getPlugin("ApiCraft");
            apiCraftDetected = apiCraft != null;
            if (apiCraftDetected) {
                BukkitLogger.info("'ApiCraft' plugin detected.");
                listener = new ApiCraftListener();

                setEnabled(true);

                server.getPluginManager().registerEvent(Event.Type.CUSTOM_EVENT, listener, Priority.Lowest, plugin);
            } else {
                BukkitLogger.info("'ApiCraft' plugin is NOT installed");
            }
        }
    }

    public void setEnabled(boolean enabled) {
        if (apiCraftDetected) {
            if (this.enabled && !enabled) {
                BukkitLogger.info("Disabling ApiCraft support...");
            } else if (!this.enabled && enabled) {
                BukkitLogger.info("Enabling ApiCraft support...");
            }
            this.enabled = enabled;
            if (enabled) {
                listener.enable();
                PluginInfo.exportController.addApiCraftListener(listener);
            } else {
                listener.disable();
                PluginInfo.exportController.removeApiCraftListener(listener);
            }
        }
    }
}
