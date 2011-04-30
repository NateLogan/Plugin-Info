package org.petricek.bukkit.plugininfo;

import com.jascotty2.JMinecraftFontWidthCalculator;
import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Michal Petříček
 */
public class Commands {

    public static final ChatColor nameColor = ChatColor.YELLOW;
    public static final ChatColor versionColor = ChatColor.AQUA;
    public static final ChatColor textColor = ChatColor.WHITE;
    public static final ChatColor headertColor = ChatColor.DARK_GREEN;
    public static final ChatColor commandColor = ChatColor.YELLOW;
    public static final ChatColor errorColor = ChatColor.RED;
    private final Permissions permissions;

    public Commands(Server server) {
        permissions = new Permissions(server);
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
                || permissions.checkPermission(player, Permissions.PERMISSION_RELOAD)) {
            sender.sendMessage(headertColor.toString() + "[Plugin Info HELP]");
            if (permissions.checkPermission(player, Permissions.PERMISSION_VIEW)) {
                sender.sendMessage(commandColor.toString() + (console ? "  " : "/") + "plugi list|l all" + textColor.toString() + " - List all plugins");
                sender.sendMessage(commandColor.toString() + (console ? "  " : "/") + "plugi list|l [#]" + textColor.toString() + " - Detailed list of " + PluginInfo.settings.entriesPerPage + " plugins at a time");
            }
            if (permissions.checkPermission(player, Permissions.PERMISSION_EXPORT) || permissions.checkPermission(player, Permissions.PERMISSION_EXPORT_ALL)) {
                sender.sendMessage(commandColor.toString() + (console ? "  " : "/") + "plugi export|e" + textColor.toString() + " - Exports plugins info");
                sender.sendMessage(commandColor.toString() + (console ? "  " : "/") + "plugi export|e list|l" + textColor.toString() + " - List of available export file types.");
            }
            if (permissions.checkPermission(player, Permissions.PERMISSION_EXPORT_ALL)) {
                sender.sendMessage(commandColor.toString() + (console ? "  " : "/") + "plugi export|e [param]" + textColor.toString() + " - Exports plugins info to [param]'s file");
                sender.sendMessage(commandColor.toString() + (console ? "  " : "/") + "plugi export|e all" + textColor.toString() + " - Exports plugins info into all available files");
            }
            if (permissions.checkPermission(player, Permissions.PERMISSION_RELOAD)) {
                sender.sendMessage(commandColor.toString() + (console ? "  " : "/") + "plugi reload|r" + textColor.toString() + " - Reloads settings");
            }
        } else {
            sender.sendMessage(errorColor.toString() + "You are not allowed to use command " + commandColor.toString() + (console ? "" : "/") + "plugi [help]");
        }
    }

    public void getAllPlugins(CommandSender sender, ArrayList<PluginData> plugins) {
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }

        if (permissions.checkPermission(player, Permissions.PERMISSION_VIEW)){
            StringBuilder out = new StringBuilder();
            for (PluginData pluginInfo : plugins) {
                String t = nameColor.toString() + pluginInfo.getName() + " "
                        + versionColor.toString() + pluginInfo.getVersion() + textColor.toString() + "; ";
                out.append(t);
            }
            sender.sendMessage(out.substring(0, out.length() - 2));
        } else {
            sender.sendMessage(errorColor.toString() + "You are not allowed to use command " + commandColor.toString() + "/plugi list all");
        }

    }

    public void getPlugins(CommandSender sender, ArrayList<PluginData> plugins, int page) {
        Player player = null;
        boolean console = true;
        if (sender instanceof Player) {
            player = (Player) sender;
            console = false;
        }

        if (permissions.checkPermission(player, Permissions.PERMISSION_VIEW)){
            int entriesPerPage = PluginInfo.settings.entriesPerPage;
            if (page < 1 || page > plugins.size() / entriesPerPage + 1) {
                sender.sendMessage(errorColor.toString() + "Page " + page + " does not exist");
            } else {
                sender.sendMessage(headertColor.toString() + "[Plugin Info], page ["
                        + nameColor.toString() + (page) + "/" + (plugins.size() / entriesPerPage + 1) + headertColor.toString() + "]");
                page -= 1;
                final int maxNameLength = getMaxNameLength(plugins);
                final int longestNameWidth = getLongestNameWidth(plugins);
                for (int i = page * entriesPerPage; i < page * entriesPerPage + entriesPerPage && i < plugins.size(); i++) {
                    sender.sendMessage(console ? getLineConsole(plugins.get(i), maxNameLength) : getLineGame(plugins.get(i), longestNameWidth));
                }
            }
        } else {
            sender.sendMessage(errorColor.toString() + "You are not allowed to use command " + commandColor.toString() + "/plugi list [#]");
        }
    }

    public void export(CommandSender sender, ArrayList<PluginData> plugins, ServerData serverData, String param) {
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }

        if (permissions.checkPermission(player, Permissions.PERMISSION_EXPORT) || permissions.checkPermission(player, Permissions.PERMISSION_EXPORT_ALL)){
            if (param == null || param.isEmpty()) {
                boolean save = DataExport.save(plugins, serverData, true, false);
                sender.sendMessage(headertColor.toString() + "Export " + (save ? "succesful" : errorColor.toString() + "failed"));
            } else if (param.equalsIgnoreCase("list") || param.equalsIgnoreCase("l")) {
                sender.sendMessage(headertColor.toString() + "Available exports: " + nameColor.toString() + "xml txt" /*+ " html"*/);
            } else if (param.equalsIgnoreCase("all") || param.equalsIgnoreCase("force")) {
                if (permissions.checkPermission(player, Permissions.PERMISSION_EXPORT_ALL)){
                    boolean save = DataExport.save(plugins, serverData, true, true);
                    sender.sendMessage(headertColor.toString() + "Export " + (save ? "succesful" : errorColor.toString() + "failed"));
                } else {
                    sender.sendMessage(errorColor.toString() + "You are not allowed to use command " + commandColor.toString() + "/plugi export " + param);
                }
            } else if (param.equalsIgnoreCase("xml")) {
                if (PluginInfo.settings.xmlSaveEnabled || permissions.checkPermission(player, Permissions.PERMISSION_EXPORT_ALL)){
                    boolean save = DataExport.instance.saveXML(plugins, serverData, true);
                    sender.sendMessage(headertColor.toString() + "XML export " + (save ? "succesful" : errorColor.toString() + "failed"));
                } else {
                    sender.sendMessage(errorColor.toString() + "You are not allowed to use command " + commandColor.toString() + "/plugi export " + param);
                }
            } else if (param.equalsIgnoreCase("txt")) {
                if (PluginInfo.settings.txtSaveEnabled || permissions.checkPermission(player, Permissions.PERMISSION_EXPORT_ALL)){
                    boolean save = DataExport.instance.saveTXT(plugins, serverData);
                    sender.sendMessage(headertColor.toString() + "TXT export " + (save ? "succesful" : errorColor.toString() + "failed"));
                } else {
                    sender.sendMessage(errorColor.toString() + "You are not allowed to use command " + commandColor.toString() + "/plugi export " + param);
                }
            } /*else if (param.equalsIgnoreCase("html")) {
                if (PluginInfo.settings.htmlSaveEnabled || permissions.checkPermission(player, Permissions.PERMISSION_EXPORT_ALL)){
                    sender.sendMessage(errorColor.toString() + "Not yet available");
                } else {
                    sender.sendMessage(errorColor.toString() + "You are not allowed to use command " + commandColor.toString() + "/plugi export " + param);
                }
            }*/ else {
                sender.sendMessage(errorColor.toString() + "Unknown export format.");
            }
        } else {
            sender.sendMessage(errorColor.toString() + "You are not allowed to use command " + commandColor.toString() + "/plugi export " + param);
        }
    }

    public void reload(CommandSender sender) {
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }
        
        if (permissions.checkPermission(player, Permissions.PERMISSION_EXPORT)){
            PluginInfo.settings.initialize();
            PluginInfo.settingsXml.initialize();
            sender.sendMessage(headertColor.toString() + "[PluginInfo] Settings reloaded");
        } else {
            sender.sendMessage(errorColor.toString() + "You are not allowed to use command " + commandColor.toString() + "/plugi reload");
        }

    }

    //--------------------------------------------------------------------------
    private String getLineConsole(PluginData pluginData, int maxNameLength) {
        StringBuilder out = new StringBuilder(nameColor.toString() + pluginData.getName());
        for (int i = out.length(); i < maxNameLength; i++) {
            out.append(" ");
        }
        out.append(textColor.toString()).append(" - ");
        out.append(versionColor.toString()).append(pluginData.getVersion());
        return out.toString();
    }

    private String getLineGame(PluginData pluginData, int maxNameWidth) {
        StringBuilder out = new StringBuilder(nameColor.toString() + pluginData.getName());
        for (int i = JMinecraftFontWidthCalculator.getStringWidth(pluginData.getName());
                i < maxNameWidth; i += JMinecraftFontWidthCalculator.getCharWidth(' ')) {
            out.append(' ');
        }
        out.append(textColor.toString()).append(" - ");
        out.append(versionColor.toString()).append(pluginData.getVersion());
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
}
