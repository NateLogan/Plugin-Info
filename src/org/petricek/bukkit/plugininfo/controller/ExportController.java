package org.petricek.bukkit.plugininfo.controller;

import java.io.File;
import java.util.ArrayList;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.petricek.bukkit.plugininfo.BukkitLogger;
import org.petricek.bukkit.plugininfo.PluginInfo;
import org.petricek.bukkit.plugininfo.model.DataExport;
import org.petricek.bukkit.plugininfo.integration.FileManager;
import org.petricek.bukkit.plugininfo.integration.FtpManager;
import org.petricek.bukkit.plugininfo.model.ExportType;
import org.petricek.bukkit.plugininfo.model.PluginData;
import org.petricek.bukkit.plugininfo.model.ServerData;
import org.petricek.bukkit.plugininfo.model.XmlDocument;

/**
 *
 * @author Michal Petříček
 */
public class ExportController {

    private final Server server;
    private ApiCraftListener apiCraftListener = null;
    private final DataExport dataExport = DataExport.instance;

    public ExportController(Server server) {
        this.server = server;
    }

    public void export(ExportType exportType, ArrayList<PluginData> plugins, ServerData serverData) {
        export(null, exportType, plugins, serverData);
    }

    public void export(CommandSender sender, ExportType type, ArrayList<PluginData> plugins, ServerData serverData) {
        boolean success = true;
        try {
            switch (type) {
                case TXT:
                    String txt = dataExport.getTxt(plugins, serverData);
                    success = FileManager.save(PluginInfo.settings.getTxtOutputFile(), txt);
                    MessageController.sendMessage(sender, PluginInfo.headertColor.toString() + type + " export " + (success ? "succesful" : PluginInfo.errorColor.toString() + "failed"));
                    if (PluginInfo.settings.ftpAutoUpload) {
                        upload(sender, type);
                    }
                    break;
                case XML:
                    XmlDocument xmlDoc = dataExport.getXml(plugins, serverData, true);
                    success = FileManager.saveXml(PluginInfo.settings.getXmlOutputFile(), xmlDoc.getXmlDoc());
                    MessageController.sendMessage(sender, PluginInfo.headertColor.toString() + type + " export " + (success ? "succesful" : PluginInfo.errorColor.toString() + "failed"));
                    if (PluginInfo.settings.enableApiCraft && apiCraftListener != null) {
                        apiCraftListener.setXml(xmlDoc.getApiCraftXml());
                    }
                    if (success) {
                        if (PluginInfo.settings.ftpAutoUpload) {
                            upload(sender, type);
                        }
                    }
                    break;
                case DEFAULT:
                    if (PluginInfo.settings.txtSaveEnabled) {
                        export(sender, ExportType.TXT, plugins, serverData);
                    }
                    if (PluginInfo.settings.xmlSaveEnabled) {
                        export(sender, ExportType.XML, plugins, serverData);
                    }
                    return;
                case ALL:
                    export(sender, ExportType.TXT, plugins, serverData);
                    export(sender, ExportType.XML, plugins, serverData);
                    return;
                default:
                    MessageController.sendMessage(sender, PluginInfo.errorColor.toString() + "Nothing to export");
                    return;
            }
        } catch (Exception ex) {
            BukkitLogger.severe("Unknown error while exporting", ex);
        }
    }

    public void upload(CommandSender sender, ExportType type) {
        if (!PluginInfo.settings.ftpEnabled) {
            return;
        }

        try {
            switch (type) {
                case TXT:
                    String txtOutputFile = PluginInfo.settings.getTxtOutputFile();
                    if (FileManager.fileExists(txtOutputFile)) {
                        Thread txtUploader = new Thread(new Uploader(sender, new File(txtOutputFile), ExportType.TXT));
                        txtUploader.start();
                        MessageController.sendMessage(sender, PluginInfo.headertColor.toString() + "TXT upload started");
                    }
                    break;
                case XML:
                    String xmlOutputFile = PluginInfo.settings.getXmlOutputFile();
                    if (FileManager.fileExists(xmlOutputFile)) {
                        Thread xmlUploader = new Thread(new Uploader(sender, new File(xmlOutputFile), ExportType.XML));
                        xmlUploader.start();
                        MessageController.sendMessage(sender, PluginInfo.headertColor.toString() + "XML upload started");
                    }
                    break;
                case DEFAULT:
                    if (PluginInfo.settings.txtSaveEnabled) {
                        upload(sender, ExportType.TXT);
                    }
                    if (PluginInfo.settings.xmlSaveEnabled) {
                        upload(sender, ExportType.XML);
                    }
                    return;
                case ALL:
                    upload(sender, ExportType.TXT);
                    upload(sender, ExportType.XML);
                    return;
                default:
                    MessageController.sendMessage(sender, PluginInfo.headertColor.toString() + "Nothing to upload");
                    return;
            }
        } catch (Exception ex) {
            BukkitLogger.severe("Unknown error while uploading", ex);
        }
    }

    public void addApiCraftListener(ApiCraftListener apiCraftListener) {
        this.apiCraftListener = apiCraftListener;
    }

    public void removeApiCraftListener(ApiCraftListener apiCraftListener) {
        this.apiCraftListener = null;
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
            if (path != null && !path.isEmpty() && !path.endsWith("/")) {
                path += "/";
            }
            boolean success = FtpManager.upload(PluginInfo.settings.ftpServer,
                    PluginInfo.settings.ftpUsername, PluginInfo.settings.ftpPasswd,
                    path + file.getName(), file);
            MessageController.sendMessage(sender, PluginInfo.headertColor.toString() + type + " upload " + (success ? "finished" : PluginInfo.errorColor.toString() + "failed"));
        }
    }
}
