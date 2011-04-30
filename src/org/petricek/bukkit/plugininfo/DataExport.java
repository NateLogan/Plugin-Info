package org.petricek.bukkit.plugininfo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

/**
 *
 * @author Michal Petříček
 */
public class DataExport {

    public static DataExport instance = new DataExport();

    public static boolean onEnableSave(ArrayList<PluginData> pluginList, ServerData serverData) {
        return save(pluginList, serverData, false, false);
    }

    public static boolean save(ArrayList<PluginData> pluginList, ServerData serverData, boolean printPluginStates, boolean force) {
        boolean success = true;

        if (PluginInfo.settings.xmlSaveEnabled || force) {
            BukkitLogger.info("XML export started...");
            if (!instance.saveXML(pluginList, serverData, printPluginStates)) {
                success = false;
                BukkitLogger.info("XML export failed.");
            } else {
                BukkitLogger.info("XML export successfully finished.");
            }
        }
        if (PluginInfo.settings.txtSaveEnabled || force) {
            BukkitLogger.info("TXT export started...");
            if (!instance.saveTXT(pluginList, serverData)) {
                success = false;
                BukkitLogger.info("TXT export failed.");
            } else {
                BukkitLogger.info("TXT export successfully finished.");
            }
        }
        return success;
    }

    public boolean saveXML(ArrayList<PluginData> pluginList, ServerData serverInfo, boolean printPluginStates) {
        final SettingsXml settingsXml = PluginInfo.settingsXml;
        final Settings settings = PluginInfo.settings;

        if(settings.xmlFileName == null || settings.xmlFileName.isEmpty()){
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
            writer = new MyWriter(file);

            writer.writeLine("<?xml version=\"1.0\"?>");

            if(!(settingsXml.xslt == null) && !settingsXml.xslt.isEmpty()){
                writer.writeLine("<?xml-stylesheet type=\"text/xsl\" href=\"" + settingsXml.xslt +"\"?>");
            }

            writer.writeLine("<!-- " + getDateStamp() + " " + getTimeStamp() + " -->");
            writer.writeLine("<plugin-info>");
            writer.levelIncrease();
            {
                if (settingsXml.printGeneratedTime) {
                    writer.writeLine("<generated>");
                    writer.levelIncrease();
                    {
                        writer.writeLine("<date>" + getDateStamp() + "</date>");
                        writer.writeLine("<time>" + getTimeStamp() + "</time>");
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

                            if (printPluginStates && settingsXml.printPluginEnabled) {
                                writer.writeLine("<plugin enabled=" + pluginInfo.isEnabled() + ">");
                            } else {
                                writer.writeLine("<plugin>");
                            }
                            writer.levelIncrease();
                            {
                                //name:
                                writer.writeLine("<name>" + normalizeXml(pluginInfo.getName()) + "</name>");
                                //version:
                                if (settingsXml.printVersion && pluginInfo.getVersion() != null && !pluginInfo.getVersion().isEmpty()) {
                                    writer.writeLine("<version>" + normalizeXml(pluginInfo.getVersion()) + "</version>");
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
                                                                printCommandDetail((String) entryCommand.getKey(), (String) commandDetailKey, (String) commandDetailValue, writer);
                                                            } else if (commandDetailValue instanceof ArrayList) {
                                                                for (Object commandDetailValuePart : (ArrayList) commandDetailValue) {
                                                                    printCommandDetail((String) entryCommand.getKey(), (String) commandDetailKey, (String) commandDetailValuePart, writer);
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
            if(writer != null) writer.close();
            file.delete();
        } finally {
            if(writer != null) writer.close();
        }

        return success;
    }

    public boolean saveTXT(ArrayList<PluginData> pluginList, ServerData serverInfo) {
        final SettingsTxt settingsTxt = PluginInfo.settingsTxt;
        final Settings settings = PluginInfo.settings;

        if(settings.txtFileName == null || settings.txtFileName.isEmpty()){
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
            writer = new MyWriter(file);
            if (settingsTxt.printComments && settingsTxt.printTimeStamp) {
                writer.writeLine(settingsTxt.commentsChar + getDateStamp() + " " + getTimeStamp());
                writer.writeLine();
            }

            if (settingsTxt.printServerInfo || settingsTxt.printMinecraftServerInfo || settingsTxt.printBukkitInfo) {
                if (settingsTxt.printComments) writer.writeLine(settingsTxt.commentsChar + "Server info:");
                if (settingsTxt.printServerInfo) {
                    writer.writeLine("Server name" + settingsTxt.delimiter + serverInfo.getServerName());
                    writer.writeLine("Server port" + settingsTxt.delimiter + serverInfo.getServerPort());
                }
                if (settingsTxt.printMinecraftServerInfo){
                    writer.writeLine("Minecraft version" + settingsTxt.delimiter + serverInfo.getMinecraftVersion());
                }
                if (settingsTxt.printBukkitInfo){
                    writer.writeLine("Bukkit build" + settingsTxt.delimiter + serverInfo.getBukkitVersion());
                    writer.writeLine("Bukkit version" + settingsTxt.delimiter + serverInfo.getBukkitName());
                }
                writer.writeLine();
            }

            if (settingsTxt.printPluginInfo) {
                if (settingsTxt.printComments) writer.writeLine(settingsTxt.commentsChar + "Plugins info:");
                for (PluginData pluginData : pluginList) {
                    writer.writeLine(pluginData.getName() + settingsTxt.delimiter + pluginData.getVersion());
                }
            }
        } catch (Exception ex) {
            success = false;
            BukkitLogger.severe("Unable to write data into file " + file.getAbsolutePath(), ex);
            if(writer != null) writer.close();
            file.delete();
        } finally {
            if(writer != null) writer.close();
        }

        return success;
    }

    public static String getDateStamp() {
        Calendar cal = Calendar.getInstance();
        String out = "";
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        out += year + "-";
        out += (month < 10 ? "0" + month : month) + "-";
        out += (day < 10 ? "0" + day : day);

        return out;
    }

    public static String getTimeStamp() {
        Calendar cal = Calendar.getInstance();
        String out = "";
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);
        int sec = cal.get(Calendar.SECOND);

        out += (hour < 10 ? "0" + hour : hour) + ":";
        out += (min < 10 ? "0" + min : min) + ":";
        out += (sec < 10 ? "0" + sec : sec);

        return out;
    }

    private static String normalizeXml(String s) {
        while (s.endsWith("\r") || s.endsWith("\n")) {
            s = s.substring(0, s.length() - 1);
        }
        s = s.replace("<", "&lt;");
        s = s.replace(">", "&gt;");
        s = s.replace("&", "&amp;");
        return s;
    }

    private static void printCommandDetail(String command, String key, String value, MyWriter writer) throws IOException {
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

    private class MyWriter {

        private BufferedWriter writer;
        private int level = 0;
        private static final String ONE_LEVEL = "    ";

        public MyWriter(File file) throws IOException {
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

        private void levelIncrease() {
            level++;
        }

        private void levelDecrease() {
            if (level > 0) {
                level--;
            }
        }

        private String getLevel() {
            String lvl = "";
            for (int i = 0; i < level; i++) {
                lvl += ONE_LEVEL;
            }
            return lvl;
        }
    }
}
