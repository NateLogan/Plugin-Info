package org.petricek.bukkit.plugininfo.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.petricek.bukkit.plugininfo.BukkitLogger;
import org.petricek.bukkit.plugininfo.PluginInfo;
import org.petricek.bukkit.plugininfo.SettingsTxt;
import org.petricek.bukkit.plugininfo.SettingsXml;
import org.petricek.bukkit.plugininfo.core.XmlNode;
import org.petricek.bukkit.plugininfo.core.XmlNodeList;
import org.petricek.bukkit.plugininfo.core.XmlNodeFinal;
import org.petricek.bukkit.plugininfo.core.XmlNodeMap;
import org.petricek.bukkit.plugininfo.utils.Utils;

/**
 *
 * @author Michal Petříček
 */
public class DataExport {

    public static DataExport instance = new DataExport();

    public XmlDocument getXml(ArrayList<PluginData> pluginList, ServerData serverInfo, boolean printPluginStates) {
        final SettingsXml settingsXml = PluginInfo.settingsXml;

        XmlNodeMap root = new XmlNodeMap("plugin-info");

        if (settingsXml.printGeneratedTime) {
            XmlNodeMap generated = new XmlNodeMap("generated");
            {
                generated.insertElement(new XmlNodeFinal("date", Utils.getDateStamp()));
                generated.insertElement(new XmlNodeFinal("time", Utils.getTimeStamp()));
            }
            root.insertElement(generated);
        }

        if (settingsXml.printServerInfo || settingsXml.printMinecraftServerInfo || settingsXml.printBukkitInfo) {
            XmlNodeMap server = new XmlNodeMap("server");

            if (settingsXml.printServerInfo) {
                server.insertElement(new XmlNodeFinal("name", serverInfo.getServerName()));
                server.insertElement(new XmlNodeFinal("port", serverInfo.getServerPort()));
            }

            if (settingsXml.printMinecraftServerInfo) {
                XmlNodeMap minecraftServer = new XmlNodeMap("minecraft-server");
                {
                    minecraftServer.insertElement(new XmlNodeFinal("version", serverInfo.getMinecraftVersion()));
                }
                server.insertElement(minecraftServer);
            }

            if (settingsXml.printBukkitInfo) {
                XmlNodeMap bukkit = new XmlNodeMap("bukkit");
                {
                    bukkit.insertElement(new XmlNodeFinal("build", serverInfo.getBukkitVersion()));
                    bukkit.insertElement(new XmlNodeFinal("version", serverInfo.getBukkitName()));
                }
                server.insertElement(bukkit);
            }

            root.insertElement(server);
        }

        if (settingsXml.printPlugins) {
            XmlNodeList plugins = new XmlNodeList("plugins");

            int enabledPlugins = 0;
            for (PluginData pluginInfo : pluginList) {
                XmlNodeMap plugin = new XmlNodeMap("plugin");
                {
                    //name:
                    plugin.insertElement(new XmlNodeFinal("name", pluginInfo.getName()));
                    //version:
                    if (settingsXml.printVersion && pluginInfo.getVersion() != null && !pluginInfo.getVersion().isEmpty()) {
                        plugin.insertElement(new XmlNodeFinal("version", pluginInfo.getVersion()));
                    }
                    //enabled:
                    if (printPluginStates && settingsXml.printPluginEnabled) {
                        plugin.insertElement(new XmlNodeFinal("enabled", String.valueOf(pluginInfo.isEnabled())));
                    }
                    //fullname:
                    if (settingsXml.printFullname && pluginInfo.getFullName() != null && !pluginInfo.getFullName().isEmpty()) {
                        plugin.insertElement(new XmlNodeFinal("fullname", pluginInfo.getFullName()));
                    }
                    //desc:
                    if (settingsXml.printDesc && pluginInfo.getDescription() != null && !pluginInfo.getDescription().isEmpty()) {
                        plugin.insertElement(new XmlNodeFinal("description", pluginInfo.getDescription()));
                    }
                    //web:
                    if (settingsXml.printWeb && pluginInfo.getWebsite() != null && !pluginInfo.getWebsite().isEmpty()) {
                        plugin.insertElement(new XmlNodeFinal("website", pluginInfo.getWebsite()));
                    }
                    //database-enabled:
                    if (settingsXml.printDatabaseEnabled && pluginInfo.isDatabaseEnabled()) {
                        plugin.insertElement(new XmlNodeFinal("database-enabled", String.valueOf(pluginInfo.isDatabaseEnabled())));
                    }
                    //author(s):
                    if (settingsXml.printAuthors && pluginInfo.getAuthors() != null && !pluginInfo.getAuthors().isEmpty()) {
                        if (pluginInfo.getAuthors().size() == 1) {
                            plugin.insertElement(new XmlNodeFinal("author", pluginInfo.getAuthors().get(0)));
                        } else {
                            XmlNodeList authors = new XmlNodeList("authors");
                            for (String author : pluginInfo.getAuthors()) {
                                authors.insertElement(new XmlNodeFinal("author", author));
                            }
                            plugin.insertElement(authors);
                        }
                    }
                    //comands:
                    if (settingsXml.printCommands && pluginInfo.getCommands() != null && !((Map) pluginInfo.getCommands()).isEmpty()) {
                        XmlNodeList commands = new XmlNodeList("commands");

                        LinkedHashMap CommandsList = (LinkedHashMap) pluginInfo.getCommands();
                        if (settingsXml.printCommandsDetails) {
                            for (Object o1 : CommandsList.entrySet()) {
                                Entry e1 = (Entry) o1;
                                XmlNodeList command = new XmlNodeList("command");
                                {
                                    command.insertElement(new XmlNodeFinal("name", e1.getKey().toString()));
                                    if (!(e1.getValue() instanceof Map)) {   //DEBUG
                                        BukkitLogger.warning("Parsing error (command).");
                                    }
                                    for (Object o2 : ((Map) e1.getValue()).entrySet()) {
                                        Entry e2 = (Entry) o2;
                                        if (e2.getValue() != null) {
                                            String e2Value = e2.getValue().toString().replaceAll("<command>", e1.getKey().toString());

                                            String[] e2Values = e2Value.split("[\n]");
                                            for (String e2String : e2Values) {
                                                if (!e2String.isEmpty()) {
                                                    command.insertElement(new XmlNodeFinal(e2.getKey().toString(), e2String));
                                                }
                                            }
                                        }
                                    }

                                }
                                commands.insertElement(command);
                            }
                        } else {
                            for (Object object : CommandsList.keySet()) {
                                commands.insertElement(new XmlNodeFinal("command", object.toString()));
                            }
                        }
                        plugin.insertElement(commands);
                    }

                    //depend:
                    if (settingsXml.printDepend && pluginInfo.getDepend() != null
                            && pluginInfo.getDepend() instanceof ArrayList
                            && !((ArrayList) pluginInfo.getDepend()).isEmpty()) {
                        XmlNode depend = convertDataStructure("depend", "depend-plugin", pluginInfo.getDepend());
                        plugin.insertElement(depend);
                    }
                }
                plugins.insertElement(plugin);

                if (pluginInfo.isEnabled()) {
                    enabledPlugins++;
                }
            }

            {
                XmlNodeMap stats = new XmlNodeMap("stats");
                stats.insertElement(new XmlNodeFinal("plugins-count", String.valueOf(pluginList.size())));
                if (printPluginStates) {
                    stats.insertElement(new XmlNodeFinal("enabled-plugins", String.valueOf(enabledPlugins)));
                    stats.insertElement(new XmlNodeFinal("disabled-plugins", String.valueOf(pluginList.size() - enabledPlugins)));
                }
                plugins.insertElement(stats);
            }

            root.insertElement(plugins);
        }

        return new XmlDocument(root);
    }

    private XmlNode convertDataStructure(String name, Object o) {
        return convertDataStructure(name, name, o);
    }

    private XmlNode convertDataStructure(String nameParent, String nameChild, Object o) {
        if (o instanceof String) {
            return new XmlNodeFinal(nameParent, (String) o);
        }
        if (o instanceof List) {
            XmlNodeList nodeList = new XmlNodeList(nameParent);
            for (Object object : (List) o) {
                nodeList.insertElement(convertDataStructure(nameChild, object));
            }
            return nodeList;
        }
        if (o instanceof Map) {
            XmlNodeMap nodeMap = new XmlNodeMap(nameParent);
            Map m = (Map) o;
            Set keySet = m.keySet();
            for (Object key : keySet) {
                nodeMap.insertElement(convertDataStructure(key.toString(), m.get(key)));
            }
            return nodeMap;
        }
        return null;
    }

    public String getTxt(ArrayList<PluginData> pluginList, ServerData serverInfo) {
        final SettingsTxt settingsTxt = PluginInfo.settingsTxt;

        StringBuilder string = new StringBuilder();
        
        if (settingsTxt.printComments && settingsTxt.printTimeStamp) {
            string.append(settingsTxt.commentsChar).append(Utils.getDateStamp()).append(" ").append(Utils.getTimeStamp()).append("\n");
            string.append("\n");
        }

        if (settingsTxt.printServerInfo || settingsTxt.printMinecraftServerInfo || settingsTxt.printBukkitInfo) {
            if (settingsTxt.printComments) {
                string.append(settingsTxt.commentsChar).append("Server info:" + "\n");
            }
            if (settingsTxt.printServerInfo) {
                string.append("Server name").append(settingsTxt.delimiter).append(serverInfo.getServerName()).append("\n");
                string.append("Server port").append(settingsTxt.delimiter).append(serverInfo.getServerPort()).append("\n");
            }
            if (settingsTxt.printMinecraftServerInfo) {
                string.append("Minecraft version").append(settingsTxt.delimiter).append(serverInfo.getMinecraftVersion()).append("\n");
            }
            if (settingsTxt.printBukkitInfo) {
                string.append("Bukkit build").append(settingsTxt.delimiter).append(serverInfo.getBukkitVersion()).append("\n");
                string.append("Bukkit version").append(settingsTxt.delimiter).append(serverInfo.getBukkitName()).append("\n");
            }
            string.append("\n");
        }

        if (settingsTxt.printPluginInfo) {
            if (settingsTxt.printComments) {
                string.append(settingsTxt.commentsChar).append("Plugins info:" + "\n");
            }
            for (PluginData pluginData : pluginList) {
                string.append(pluginData.getName()).append(settingsTxt.delimiter).append(pluginData.getVersion()).append("\n");
            }
        }
        
        return string.toString().substring(0, string.length() - 1);
    }

}
