package org.petricek.bukkit.plugininfo.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.petricek.bukkit.plugininfo.PluginInfo;
import org.petricek.bukkit.plugininfo.integration.DataExport;
import org.petricek.bukkit.plugininfo.integration.FileManager;
import org.petricek.bukkit.plugininfo.integration.FtpManager;
import org.petricek.bukkit.plugininfo.model.ExportType;
import org.petricek.bukkit.plugininfo.model.PluginData;
import org.petricek.bukkit.plugininfo.model.ServerData;

/**
 *
 * @author Michal Petříček
 */
public class ExportController {

    private Server server;

    public ExportController(Server server) {
        this.server = server;
    }

    public void export(ExportType exportType) {
        export(null, exportType);
    }

    public void export(CommandSender sender, ExportType type) {
        boolean succes = true;
        switch (type) {
            case TXT:
                succes = DataExport.instance.saveTXT(getPluginVersionsList(), getServerInfo());
                MessageController.sendMessage(sender, PluginInfo.headertColor.toString() + type + " export " + (succes ? "succesful" : PluginInfo.errorColor.toString() + "failed"));
                if(PluginInfo.settings.ftpAutoUpload) upload(sender, type);
                break;
            case XML:
                succes = DataExport.instance.saveXML(getPluginVersionsList(), getServerInfo(), true);
                MessageController.sendMessage(sender, PluginInfo.headertColor.toString() + type + " export " + (succes ? "succesful" : PluginInfo.errorColor.toString() + "failed"));
                if(PluginInfo.settings.ftpAutoUpload) upload(sender, type);
                break;
            case DEFAULT:
                if (PluginInfo.settings.txtSaveEnabled) export(sender, ExportType.TXT);
                if (PluginInfo.settings.xmlSaveEnabled) export(sender, ExportType.XML);
                return;
            case ALL:
                export(sender, ExportType.TXT);
                export(sender, ExportType.XML);
                return;
            default:
                MessageController.sendMessage(sender, PluginInfo.errorColor.toString() + "Nothing to export");
                return;
        }
    }

    public void upload(CommandSender sender, ExportType type) {
        if (!PluginInfo.settings.ftpEnabled) {
            return;
        }

        switch (type) {
            case TXT:
                String txtOutputFile = PluginInfo.settings.getTxtOutputFile();
                if(FileManager.fileExists(txtOutputFile)){
                    Thread txtUploader = new Thread(new Uploader(sender, new File(txtOutputFile), ExportType.TXT));
                    txtUploader.start();
                    MessageController.sendMessage(sender, PluginInfo.headertColor.toString() + "TXT upload started");
                }
                break;
            case XML:
                String xmlOutputFile = PluginInfo.settings.getXmlOutputFile();
                if(FileManager.fileExists(xmlOutputFile)){
                    Thread xmlUploader = new Thread(new Uploader(sender, new File(xmlOutputFile), ExportType.XML));
                    xmlUploader.start();
                    MessageController.sendMessage(sender, PluginInfo.headertColor.toString() + "XML upload started");
                }
                break;
            case DEFAULT:
                if (PluginInfo.settings.txtSaveEnabled) upload(sender, ExportType.TXT);
                if (PluginInfo.settings.xmlSaveEnabled) upload(sender, ExportType.XML);
                return;
            case ALL:
                upload(sender, ExportType.TXT);
                upload(sender, ExportType.XML);
                return;
            default:
                MessageController.sendMessage(sender, PluginInfo.headertColor.toString() + "Nothing to upload");
                return;
        }
    }

    private ArrayList<PluginData> getPluginVersionsList() {
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

        return list;
    }

    private ServerData getServerInfo() {
        ServerData serverInfo = new ServerData();
        serverInfo.setVersions(server.getVersion());
        serverInfo.setServerName(server.getServerName());
        serverInfo.setServerPort(String.valueOf(server.getPort()));
        return serverInfo;
    }

    private class Uploader implements Runnable {

        private File file;
        private ExportType type;
        private final CommandSender sender;

        public Uploader(CommandSender sender, File file, ExportType type) {
            this.file = file;
            this.sender = sender;
            this.type = type;
        }

        public void run() {
            String path = PluginInfo.settings.ftpPath;
            if(path != null && !path.isEmpty() && !path.endsWith("/")) path += "/";
            boolean success = FtpManager.upload(PluginInfo.settings.ftpServer,
                    PluginInfo.settings.ftpUsername, PluginInfo.settings.ftpPasswd,
                    path + file.getName(), file);
            MessageController.sendMessage(sender, PluginInfo.headertColor.toString() + type + " upload " + (success ? "finished" : PluginInfo.errorColor.toString() + "failed"));
        }

    }
}
