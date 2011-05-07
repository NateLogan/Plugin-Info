/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.petricek.bukkit.plugininfo.controller;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.petricek.bukkit.plugininfo.model.PluginData;
import org.petricek.bukkit.plugininfo.model.ServerData;

/**
 *
 * @author Michal Petříček
 */
public class PluginController {

    private final Server server;
    
    public PluginController(Server server) {
        this.server = server;
    }
    
    public ArrayList<PluginData> getPluginVersionsList() {
        Plugin[] plugins = server.getPluginManager().getPlugins();
        ArrayList<PluginData> list = new ArrayList<PluginData>(plugins.length);

        for (Plugin plugin : plugins) {
            PluginData pluginInfo = new PluginData();
            pluginInfo.setName(plugin.getDescription().getName());
            pluginInfo.setDescription(plugin.getDescription().getDescription());
            pluginInfo.setVersion(plugin.getDescription().getVersion());
            pluginInfo.setWebsite(plugin.getDescription().getWebsite());
            pluginInfo.setAuthors(plugin.getDescription().getAuthors());
            pluginInfo.setFullName(plugin.getDescription().getFullName());
            pluginInfo.setDepend(plugin.getDescription().getDepend());
            pluginInfo.setCommands(plugin.getDescription().getCommands());
            pluginInfo.setDatabaseEnabled(plugin.getDescription().isDatabaseEnabled());
            pluginInfo.setEnabled(plugin.isEnabled());
            list.add(pluginInfo);
        }

        Collections.sort(list);
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("plugins.data"));
            oos.writeObject(list);
            oos.close();
        } catch (IOException iOException) {
        }
        
        return list;
    }

    public ServerData getServerInfo() {
        ServerData serverInfo = new ServerData();
        serverInfo.setVersions(server.getVersion());
        serverInfo.setServerName(server.getServerName());
        serverInfo.setServerPort(String.valueOf(server.getPort()));
        return serverInfo;
    }
    
}
