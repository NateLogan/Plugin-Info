package org.petricek.bukkit.plugininfo.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.petricek.bukkit.plugininfo.BukkitLogger;
import org.petricek.bukkit.plugininfo.PluginInfo;
import org.petricek.bukkit.plugininfo.Settings;
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

    @Deprecated
    public boolean saveXML(ArrayList<PluginData> pluginList, ServerData serverInfo, boolean printPluginStates) {
        final SettingsXml settingsXml = PluginInfo.settingsXml;
        final Settings settings = PluginInfo.settings;

        if (settings.xmlFileName == null || settings.xmlFileName.isEmpty()) {
            settings.xmlFileName = "plugins.xml";
            settings.save();
        }

        File file = settings.xmlOutputFolder == null || settings.xmlOutputFolder.isEmpty()
                ? new File(settings.outputFolder, settings.xmlFileName)
                : new File(settings.xmlOutputFolder, settings.xmlFileName);
        BukkitLogger.info("Saving data into file: " + file.getAbsolutePath());

        boolean success = true;
        MyWriter writer = null;
        try {
            writer = new MyWriterFile(file);

            writer.writeLine("<?xml version=\"1.0\"?>");

            if (!(settingsXml.xslt == null) && !settingsXml.xslt.isEmpty()) {
                writer.writeLine("<?xml-stylesheet type=\"text/xsl\" href=\"" + settingsXml.xslt + "\"?>");
            }

            writer.writeLine("<!-- " + Utils.getDateStamp() + " " + Utils.getTimeStamp() + " -->");
            writer.writeLine("<plugin-info>");
            writer.levelIncrease();
            {
                if (settingsXml.printGeneratedTime) {
                    writer.writeLine("<generated>");
                    writer.levelIncrease();
                    {
                        writer.writeLine("<date>" + Utils.getDateStamp() + "</date>");
                        writer.writeLine("<time>" + Utils.getTimeStamp() + "</time>");
                    }
                    writer.levelDecrease();
                    writer.writeLine("</generated>");
                }

                if (settingsXml.printServerInfo || settingsXml.printMinecraftServerInfo || settingsXml.printBukkitInfo) {
                    writer.writeLine("<server>");
                    writer.levelIncrease();
                    {
                        if (settingsXml.printServerInfo) {
                            writer.writeLine("<name>" + normalizeXml(serverInfo.getServerName()) + "</name>");
                            writer.writeLine("<port>" + normalizeXml(serverInfo.getServerPort()) + "</port>");
                        }

                        if (settingsXml.printMinecraftServerInfo) {
                            writer.writeLine("<minecraft-server>");
                            writer.levelIncrease();
                            {
                                writer.writeLine("<version>" + normalizeXml(serverInfo.getMinecraftVersion()) + "</version>");
                            }
                            writer.levelDecrease();
                            writer.writeLine("</minecraft-server>");
                        }

                        if (settingsXml.printBukkitInfo) {
                            writer.writeLine("<bukkit>");
                            {
                                writer.levelIncrease();
                                writer.writeLine("<build>" + normalizeXml(serverInfo.getBukkitVersion()) + "</build>");
                                writer.writeLine("<version>" + normalizeXml(serverInfo.getBukkitName()) + "</version>");
                            }
                            writer.levelDecrease();
                            writer.writeLine("</bukkit>");
                        }
                    }
                    writer.levelDecrease();
                    writer.writeLine("</server>");
                }

                if (settingsXml.printPlugins) {
                    writer.writeLine("<plugins>");
                    writer.levelIncrease();
                    {
                        int enabledPlugins = 0;
                        for (PluginData pluginInfo : pluginList) {
                            if (pluginInfo.isEnabled()) {
                                enabledPlugins++;
                            }

                            writer.writeLine("<plugin>");

                            writer.levelIncrease();
                            {
                                //name:
                                writer.writeLine("<name>" + normalizeXml(pluginInfo.getName()) + "</name>");
                                //version:
                                if (settingsXml.printVersion && pluginInfo.getVersion() != null && !pluginInfo.getVersion().isEmpty()) {
                                    writer.writeLine("<version>" + normalizeXml(pluginInfo.getVersion()) + "</version>");
                                }
                                //enabled:
                                if (printPluginStates && settingsXml.printPluginEnabled) {
                                    writer.writeLine("<enabled>" + pluginInfo.isEnabled() + "</enabled>");
                                }
                                //fullname:
                                if (settingsXml.printFullname && pluginInfo.getFullName() != null && !pluginInfo.getFullName().isEmpty()) {
                                    writer.writeLine("<fullname>" + normalizeXml(pluginInfo.getFullName()) + "</fullname>");
                                }
                                //desc:
                                if (settingsXml.printDesc && pluginInfo.getDescription() != null && !pluginInfo.getDescription().isEmpty()) {
                                    writer.writeLine("<description>" + normalizeXml(pluginInfo.getDescription()) + "</description>");
                                }
                                //web:
                                if (settingsXml.printWeb && pluginInfo.getWebsite() != null && !pluginInfo.getWebsite().isEmpty()) {
                                    writer.writeLine("<website>" + normalizeXml(pluginInfo.getWebsite()) + "</website>");
                                }
                                //database-enabled:
                                if (settingsXml.printDatabaseEnabled && pluginInfo.isDatabaseEnabled()) {
                                    writer.writeLine("<database-enabled>" + String.valueOf(pluginInfo.isDatabaseEnabled()) + "</database-enabled>");
                                }
                                //author(s):
                                if (settingsXml.printAuthors && pluginInfo.getAuthors() != null && !pluginInfo.getAuthors().isEmpty()) {
                                    if (pluginInfo.getAuthors().size() == 1) {
                                        writer.writeLine("<author>" + normalizeXml(pluginInfo.getAuthors().get(0)) + "</author>");
                                    } else {
                                        writer.writeLine("<authors>");
                                        writer.levelIncrease();
                                        {
                                            for (String author : pluginInfo.getAuthors()) {
                                                writer.writeLine("<author>" + normalizeXml(author) + "</author>");
                                            }
                                        }
                                        writer.levelDecrease();
                                        writer.writeLine("</authors>");
                                    }
                                }
                                //commands:
                                if (settingsXml.printCommands && pluginInfo.getCommands() != null && pluginInfo.getCommands() instanceof LinkedHashMap && !((LinkedHashMap) pluginInfo.getCommands()).isEmpty()) {
                                    writer.writeLine("<commands>");
                                    writer.levelIncrease();
                                    {
                                        LinkedHashMap commands = (LinkedHashMap) pluginInfo.getCommands();

                                        if (settingsXml.printCommandsDetails) {
                                            for (Object objectCommand : commands.entrySet()) {
                                                Entry entryCommand = (Entry) objectCommand;
                                                writer.writeLine("<command>");
                                                writer.levelIncrease();
                                                {
                                                    writer.writeLine("<name>" + normalizeXml((String) entryCommand.getKey()) + "</name>");
                                                    if (entryCommand.getValue() != null && entryCommand.getValue() instanceof LinkedHashMap) {
                                                        LinkedHashMap commandDetails = (LinkedHashMap) entryCommand.getValue();

                                                        for (Object commandDetailEntry : commandDetails.entrySet()) {
                                                            Object commandDetailKey = ((Entry) commandDetailEntry).getKey();
                                                            Object commandDetailValue = ((Entry) commandDetailEntry).getValue();

                                                            if (commandDetailKey == null || commandDetailValue == null) {
                                                                continue;
                                                            }

                                                            if (commandDetailValue instanceof String) {
                                                                printCommandDetailXml((String) entryCommand.getKey(), (String) commandDetailKey, (String) commandDetailValue, writer);
                                                            } else if (commandDetailValue instanceof ArrayList) {
                                                                for (Object commandDetailValuePart : (ArrayList) commandDetailValue) {
                                                                    printCommandDetailXml((String) entryCommand.getKey(), (String) commandDetailKey, (String) commandDetailValuePart, writer);
                                                                }
                                                            } else {
                                                                BukkitLogger.warning("Unknown entry: " + commandDetailValue + ", "
                                                                        + commandDetailKey.toString() + ", "
                                                                        + entryCommand.getKey().toString() + ", "
                                                                        + pluginInfo.getName());
                                                            }
                                                        }
                                                    }
                                                }
                                                writer.levelDecrease();
                                                writer.writeLine("</command>");
                                            }
                                        } else {
                                            for (Object objectCommand : commands.entrySet()) {
                                                Entry entry = (Entry) objectCommand;
                                                writer.writeLine("<command>" + normalizeXml(entry.getKey().toString()) + "</command>");
                                            }
                                        }
                                    }
                                    writer.levelDecrease();
                                    writer.writeLine("</commands>");
                                }
                                //depend:
                                if (settingsXml.printDepend && pluginInfo.getDepend() != null
                                        && pluginInfo.getDepend() instanceof ArrayList
                                        && !((ArrayList) pluginInfo.getDepend()).isEmpty()) {
                                    writer.writeLine("<depend>");
                                    writer.levelIncrease();
                                    {
                                        ArrayList depend = (ArrayList) pluginInfo.getDepend();
                                        for (Object dependObject : depend) {
                                            if (dependObject instanceof String) {
                                                writer.writeLine("<depend-plugin>" + dependObject + "</depend-plugin>");
                                            } else {
                                                writer.writeLine("<depend-plugin>" + dependObject.toString() + "</depend-plugin>");
                                            }
                                        }
                                    }
                                    writer.levelDecrease();
                                    writer.writeLine("</depend>");
                                }
                            }
                            writer.levelDecrease();
                            writer.writeLine("</plugin>");
                        }
                        writer.writeLine("<stats>");
                        writer.levelIncrease();
                        {
                            writer.writeLine("<plugins-count>" + pluginList.size() + "</plugins-count>");
                            if (printPluginStates) {
                                writer.writeLine("<enabled-plugins>" + enabledPlugins + "</enabled-plugins>");
                                writer.writeLine("<disabled-plugins>" + (pluginList.size() - enabledPlugins) + "</disabled-plugins>");
                            }
                        }
                        writer.levelDecrease();
                        writer.writeLine("</stats>");
                    }
                    writer.levelDecrease();
                    writer.writeLine("</plugins>");
                }
            }
            writer.levelDecrease();
            writer.writeLine("</plugin-info>");
        } catch (Exception ex) {
            success = false;
            BukkitLogger.severe("Unable to write data into file " + file.getAbsolutePath(), ex);
            if (writer != null) {
                writer.close();
            }
            file.delete();
        } finally {
            if (writer != null) {
                writer.close();
            }
        }

        return success;
    }

    @Deprecated
    public boolean saveTXT(ArrayList<PluginData> pluginList, ServerData serverInfo) {
        final SettingsTxt settingsTxt = PluginInfo.settingsTxt;
        final Settings settings = PluginInfo.settings;

        if (settings.txtFileName == null || settings.txtFileName.isEmpty()) {
            settings.txtFileName = "plugins.txt";
            settings.save();
        }

        File file = settings.txtOutputFolder == null || settings.txtOutputFolder.isEmpty()
                ? new File(settings.outputFolder, settings.txtFileName)
                : new File(settings.txtOutputFolder, settings.txtFileName);
        BukkitLogger.info("Saving data into file: " + file.getAbsolutePath());

        boolean success = true;
        MyWriter writer = null;

        try {
            writer = new MyWriterFile(file);
            if (settingsTxt.printComments && settingsTxt.printTimeStamp) {
                writer.writeLine(settingsTxt.commentsChar + Utils.getDateStamp() + " " + Utils.getTimeStamp());
                writer.writeLine();
            }

            if (settingsTxt.printServerInfo || settingsTxt.printMinecraftServerInfo || settingsTxt.printBukkitInfo) {
                if (settingsTxt.printComments) {
                    writer.writeLine(settingsTxt.commentsChar + "Server info:");
                }
                if (settingsTxt.printServerInfo) {
                    writer.writeLine("Server name" + settingsTxt.delimiter + serverInfo.getServerName());
                    writer.writeLine("Server port" + settingsTxt.delimiter + serverInfo.getServerPort());
                }
                if (settingsTxt.printMinecraftServerInfo) {
                    writer.writeLine("Minecraft version" + settingsTxt.delimiter + serverInfo.getMinecraftVersion());
                }
                if (settingsTxt.printBukkitInfo) {
                    writer.writeLine("Bukkit build" + settingsTxt.delimiter + serverInfo.getBukkitVersion());
                    writer.writeLine("Bukkit version" + settingsTxt.delimiter + serverInfo.getBukkitName());
                }
                writer.writeLine();
            }

            if (settingsTxt.printPluginInfo) {
                if (settingsTxt.printComments) {
                    writer.writeLine(settingsTxt.commentsChar + "Plugins info:");
                }
                for (PluginData pluginData : pluginList) {
                    writer.writeLine(pluginData.getName() + settingsTxt.delimiter + pluginData.getVersion());
                }
            }
        } catch (Exception ex) {
            success = false;
            BukkitLogger.severe("Unable to write data into file " + file.getAbsolutePath(), ex);
            if (writer != null) {
                writer.close();
            }
            file.delete();
        } finally {
            if (writer != null) {
                writer.close();
            }
        }

        return success;
    }

    @Deprecated
    private static String normalizeXml(String s) {
        while (s.endsWith("\r") || s.endsWith("\n")) {
            s = s.substring(0, s.length() - 1);
        }
        s = s.replace("<", "&lt;");
        s = s.replace(">", "&gt;");
        s = s.replace("&", "&amp;");
        return s;
    }

    @Deprecated
    private static void printCommandDetailXml(String command, String key, String value, MyWriter writer) throws IOException {
        key = normalizeXml(key);
        command = normalizeXml(command);
        value = value.replaceAll("<command>", command);
        value = normalizeXml(value);
        String[] split = value.split("[\n]");
        for (String string : split) {
            if (!string.isEmpty()) {
                writer.writeLine("<" + key + ">" + string + "</" + key + ">");
            }
        }
    }

    private interface MyWriter {

        public void writeLine(String line) throws IOException;

        public void writeLine() throws IOException;

        public boolean close();

        public void levelIncrease();

        public void levelDecrease();

        public String getLevel();
    }

    private class MyWriterFile implements MyWriter {

        private BufferedWriter writer;
        private int level = 0;
        private static final String ONE_LEVEL = "    ";

        public MyWriterFile(File file) throws IOException {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            writer = new BufferedWriter(new FileWriter(file));
        }

        public void writeLine(String line) throws IOException {
            writer.write(getLevel() + line);
            writer.newLine();
            writer.flush();
        }

        public void writeLine() throws IOException {
            writeLine("");
        }

        public boolean close() {
            try {
                writer.close();
            } catch (Exception ex) {
                return false;
            }
            return true;
        }

        public void levelIncrease() {
            level++;
        }

        public void levelDecrease() {
            if (level > 0) {
                level--;
            }
        }

        public String getLevel() {
            String lvl = "";
            for (int i = 0; i < level; i++) {
                lvl += ONE_LEVEL;
            }
            return lvl;
        }
    }
}
