package org.petricek.bukkit.plugininfo;

import org.petricek.bukkit.plugininfo.model.PluginData;
import com.jascotty2.JMinecraftFontWidthCalculator;
import java.util.ArrayList;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.petricek.bukkit.plugininfo.controller.ApiCraftController;
import org.petricek.bukkit.plugininfo.controller.ExportController;
import org.petricek.bukkit.plugininfo.controller.MessageController;
import org.petricek.bukkit.plugininfo.controller.PluginController;
import org.petricek.bukkit.plugininfo.integration.FileManager;
import org.petricek.bukkit.plugininfo.model.ExportType;

/**
 *
 * @author Michal Petříček
 */
public class Commands {

    private final Permissions permissions;
    private final ExportController exportController;
    private final ApiCraftController apiCraftController;
    private final PluginController pluginController;

    public Commands(Server server, ExportController exportController, PluginController pluginController, ApiCraftController apiCraftController) {
        this.permissions = new Permissions(server);
        this.exportController = exportController;
        this.pluginController = pluginController;
        this.apiCraftController = apiCraftController;
    }

    public void help(CommandSender sender) {
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }
        boolean console = player == null;

        if (permissions.checkPermission(player, Permissions.PERMISSION_VIEW)
                || permissions.checkPermission(player, Permissions.PERMISSION_EDIT)
                || permissions.checkPermission(player, Permissions.PERMISSION_EXPORT)
                || permissions.checkPermission(player, Permissions.PERMISSION_EXPORT_ALL)
                || permissions.checkPermission(player, Permissions.PERMISSION_UPLOAD)
                || permissions.checkPermission(player, Permissions.PERMISSION_RELOAD)) {
            MessageController.sendMessage(sender, PluginInfo.headertColor.toString() + "[Plugin Info HELP]");
            if (permissions.checkPermission(player, Permissions.PERMISSION_VIEW)) {
                MessageController.sendMessage(sender, PluginInfo.commandColor.toString() + (console ? "" : "/") + "plugi list|l all" + PluginInfo.textColor.toString() + " - List all plugins");
                MessageController.sendMessage(sender, PluginInfo.commandColor.toString() + (console ? "" : "/") + "plugi list|l [#]" + PluginInfo.textColor.toString() + " - Detailed list of " + PluginInfo.settings.entriesPerPage + " plugins at a time");
            }
            if (permissions.checkPermission(player, Permissions.PERMISSION_EXPORT) || permissions.checkPermission(player, Permissions.PERMISSION_EXPORT_ALL)) {
                MessageController.sendMessage(sender, PluginInfo.commandColor.toString() + (console ? "" : "/") + "plugi export|e" + PluginInfo.textColor.toString() + " - Exports plugins info");
                MessageController.sendMessage(sender, PluginInfo.commandColor.toString() + (console ? "" : "/") + "plugi export|e list|l" + PluginInfo.textColor.toString() + " - List of available export file types.");
            }
            if (permissions.checkPermission(player, Permissions.PERMISSION_EXPORT_ALL)) {
                MessageController.sendMessage(sender, PluginInfo.commandColor.toString() + (console ? "" : "/") + "plugi export|e [param]" + PluginInfo.textColor.toString() + " - Exports plugins info to [param]'s file");
                MessageController.sendMessage(sender, PluginInfo.commandColor.toString() + (console ? "" : "/") + "plugi export|e all" + PluginInfo.textColor.toString() + " - Exports plugins info into all available files");
            }
            if (permissions.checkPermission(player, Permissions.PERMISSION_UPLOAD)) {
                MessageController.sendMessage(sender, PluginInfo.commandColor.toString() + (console ? "" : "/") + "plugi upload|u" + PluginInfo.textColor.toString() + " - Uploads generated files defined in config.yml to ftp server");
                MessageController.sendMessage(sender, PluginInfo.commandColor.toString() + (console ? "" : "/") + "plugi upload|u list|l" + PluginInfo.textColor.toString() + " - List available files to upload");
                MessageController.sendMessage(sender, PluginInfo.commandColor.toString() + (console ? "" : "/") + "plugi upload|u [param]" + PluginInfo.textColor.toString() + " - Uploads generated [param] file to ftp server");
                MessageController.sendMessage(sender, PluginInfo.commandColor.toString() + (console ? "" : "/") + "plugi upload|u all" + PluginInfo.textColor.toString() + " - Uploads all generated files to ftp server");
            }
            if (permissions.checkPermission(player, Permissions.PERMISSION_RELOAD)) {
                MessageController.sendMessage(sender, PluginInfo.commandColor.toString() + (console ? "" : "/") + "plugi reload|r" + PluginInfo.textColor.toString() + " - Reloads settings");
            }
        } else {
            MessageController.sendMessage(sender, PluginInfo.errorColor.toString() + "You are not allowed to use command " + PluginInfo.commandColor.toString() + (console ? "" : "/") + "plugi [help]");
        }
    }

    public void getAllPlugins(CommandSender sender) {
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }

        if (permissions.checkPermission(player, Permissions.PERMISSION_VIEW)) {
            StringBuilder out = new StringBuilder();
            for (PluginData pluginInfo : pluginController.getPluginVersionsList()) {
                String t = PluginInfo.nameColor.toString() + pluginInfo.getName() + " "
                        + PluginInfo.versionColor.toString() + pluginInfo.getVersion() + PluginInfo.textColor.toString() + "; ";
                out.append(t);
            }
            MessageController.sendMessage(sender, out.substring(0, out.length() - 2));
        } else {
            MessageController.sendMessage(sender, PluginInfo.errorColor.toString() + "You are not allowed to use command " + PluginInfo.commandColor.toString() + "/plugi list all");
        }

    }

    public void getPlugins(CommandSender sender, int page) {
        Player player = null;
        boolean console = true;
        if (sender instanceof Player) {
            player = (Player) sender;
            console = false;
        }

        if (permissions.checkPermission(player, Permissions.PERMISSION_VIEW)) {
            ArrayList<PluginData> plugins = pluginController.getPluginVersionsList();
            
            int entriesPerPage = PluginInfo.settings.entriesPerPage;
            if (page < 1 || page > plugins.size() / entriesPerPage + 1) {
                MessageController.sendMessage(sender, PluginInfo.errorColor.toString() + "Page " + page + " does not exist");
            } else {
                MessageController.sendMessage(sender, PluginInfo.headertColor.toString() + "[Plugin Info], page ["
                        + PluginInfo.nameColor.toString() + (page) + "/" + (plugins.size() / entriesPerPage + 1) + PluginInfo.headertColor.toString() + "]");
                page -= 1;
                final int maxNameLength = getMaxNameLength(plugins);
                final int longestNameWidth = getLongestNameWidth(plugins);
                for (int i = page * entriesPerPage; i < page * entriesPerPage + entriesPerPage && i < plugins.size(); i++) {
                    MessageController.sendMessage(sender, console ? getLineConsole(plugins.get(i), maxNameLength) : getLineGame(plugins.get(i), longestNameWidth));
                }
            }
        } else {
            MessageController.sendMessage(sender, PluginInfo.errorColor.toString() + "You are not allowed to use command " + PluginInfo.commandColor.toString() + "/plugi list [#]");
        }
    }

    public void export(CommandSender sender, String param) {
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }

        if (permissions.checkPermission(player, Permissions.PERMISSION_EXPORT) || permissions.checkPermission(player, Permissions.PERMISSION_EXPORT_ALL)) {
            if (param == null || param.isEmpty()) {
                exportController.export(sender, ExportType.DEFAULT, pluginController.getPluginVersionsList(), pluginController.getServerInfo());
            } else if (param.equalsIgnoreCase("list") || param.equalsIgnoreCase("l")) {
                MessageController.sendMessage(sender, PluginInfo.headertColor.toString() + "Available exports: " + PluginInfo.nameColor.toString() + "xml txt" /*+ " html"*/);
            } else if (param.equalsIgnoreCase("all") || param.equalsIgnoreCase("force")) {
                if (permissions.checkPermission(player, Permissions.PERMISSION_EXPORT_ALL)) {
                    exportController.export(sender, ExportType.ALL, pluginController.getPluginVersionsList(), pluginController.getServerInfo());
                } else {
                    MessageController.sendMessage(sender, PluginInfo.errorColor.toString() + "You are not allowed to use command " + PluginInfo.commandColor.toString() + "/plugi export " + param);
                }
            } else if (param.equalsIgnoreCase("xml")) {
                if (PluginInfo.settings.xmlSaveEnabled || permissions.checkPermission(player, Permissions.PERMISSION_EXPORT_ALL)) {
                    exportController.export(sender, ExportType.XML, pluginController.getPluginVersionsList(), pluginController.getServerInfo());
                } else {
                    MessageController.sendMessage(sender, PluginInfo.errorColor.toString() + "You are not allowed to use command " + PluginInfo.commandColor.toString() + "/plugi export " + param);
                }
            } else if (param.equalsIgnoreCase("txt")) {
                if (PluginInfo.settings.txtSaveEnabled || permissions.checkPermission(player, Permissions.PERMISSION_EXPORT_ALL)) {
                    exportController.export(sender, ExportType.TXT, pluginController.getPluginVersionsList(), pluginController.getServerInfo());
                } else {
                    MessageController.sendMessage(sender, PluginInfo.errorColor.toString() + "You are not allowed to use command " + PluginInfo.commandColor.toString() + "/plugi export " + param);
                }
            } /*else if (param.equalsIgnoreCase("html")) {
            if (PluginInfo.settings.htmlSaveEnabled || permissions.checkPermission(player, Permissions.PERMISSION_EXPORT_ALL)){
            sender.sendMessage(errorColor.toString() + "Not yet available");
            } else {
            sender.sendMessage(errorColor.toString() + "You are not allowed to use command " + commandColor.toString() + "/plugi export " + param);
            }
            }*/ else {
                MessageController.sendMessage(sender, PluginInfo.errorColor.toString() + "Unknown export format.");
            }
        } else {
            MessageController.sendMessage(sender, PluginInfo.errorColor.toString() + "You are not allowed to use command " + PluginInfo.commandColor.toString() + "/plugi export " + param);
        }
    }

    public void reload(CommandSender sender) {
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }

        if (permissions.checkPermission(player, Permissions.PERMISSION_EXPORT)) {
            PluginInfo.settings.initialize();
            PluginInfo.settingsXml.initialize();
            settingsReloaded();
            MessageController.sendMessage(sender, PluginInfo.headertColor.toString() + "Settings reloaded");
        } else {
            MessageController.sendMessage(sender,
                    PluginInfo.errorColor.toString() + "You are not allowed to use command "
                    + PluginInfo.commandColor.toString() + "/plugi reload");
        }

    }

    public void upload(CommandSender sender, String param) {
        if(param.equalsIgnoreCase("all")){
            upload(sender, ExportType.ALL);
        }else if(param.equalsIgnoreCase("list") || param.equalsIgnoreCase("l")){
            String list = "";
            if(FileManager.fileExists(PluginInfo.settings.getTxtOutputFile())) list += "txt";
            if(FileManager.fileExists(PluginInfo.settings.getXmlOutputFile())) list += " xml";
            MessageController.sendMessage(sender, PluginInfo.headertColor.toString() + "Available uploads: "
                    + PluginInfo.nameColor.toString() + list);
        }else if(param.equalsIgnoreCase("xml")){
            upload(sender, ExportType.XML);
        }else if(param.equalsIgnoreCase("txt")){
            upload(sender, ExportType.TXT);
        }else {
            MessageController.sendMessage(sender,
                    PluginInfo.errorColor.toString() + "Unknown parameter: " + param);
        }
    }

    public void upload(CommandSender sender, ExportType type) {
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }

        if (permissions.checkPermission(player, Permissions.PERMISSION_UPLOAD)) {
            if (!PluginInfo.settings.ftpEnabled) {
                MessageController.sendMessage(sender, PluginInfo.errorColor.toString() + "FTP upload is not enabled!");
                return;
            }
            exportController.upload(sender, type);
        } else {
            MessageController.sendMessage(sender, PluginInfo.errorColor.toString() + "You are not allowed to use command " + PluginInfo.commandColor.toString() + "/plugi upload");
        }
    }

    //--------------------------------------------------------------------------
    private String getLineConsole(PluginData pluginData, int maxNameLength) {
        StringBuilder out = new StringBuilder(PluginInfo.nameColor.toString() + pluginData.getName());
        for (int i = out.length(); i < maxNameLength; i++) {
            out.append(" ");
        }
        out.append(PluginInfo.textColor.toString()).append(" - ");
        out.append(PluginInfo.versionColor.toString()).append(pluginData.getVersion());
        return out.toString();
    }

    private String getLineGame(PluginData pluginData, int maxNameWidth) {
        StringBuilder out = new StringBuilder(PluginInfo.nameColor.toString() + pluginData.getName());
        for (int i = JMinecraftFontWidthCalculator.getStringWidth(pluginData.getName());
                i < maxNameWidth; i += JMinecraftFontWidthCalculator.getCharWidth(' ')) {
            out.append(' ');
        }
        out.append(PluginInfo.textColor.toString()).append(" - ");
        out.append(PluginInfo.versionColor.toString()).append(pluginData.getVersion());
        return out.toString();
    }

    private int getMaxNameLength(ArrayList<PluginData> list) {
        int maxLength = 0;
        for (PluginData pluginInfo : list) {
            if (pluginInfo.getName().length() > maxLength) {
                maxLength = pluginInfo.getName().length();
            }
        }
        return maxLength;
    }

    private int getLongestNameWidth(ArrayList<PluginData> list) {
        int maxWidth = 0;
        for (PluginData pluginInfo : list) {
            int stringWidth = JMinecraftFontWidthCalculator.getStringWidth(pluginInfo.getName());
            if (stringWidth > maxWidth) {
                maxWidth = stringWidth;
            }
        }
        return maxWidth;
    }

    private void settingsReloaded() {
        apiCraftController.setEnabled(PluginInfo.settings.enableApiCraft);
    }

}
