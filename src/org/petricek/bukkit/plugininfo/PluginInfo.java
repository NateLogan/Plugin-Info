package org.petricek.bukkit.plugininfo;

import me.taylorkelly.help.Help;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.petricek.bukkit.plugininfo.controller.ApiCraftController;
import org.petricek.bukkit.plugininfo.controller.ExportController;
import org.petricek.bukkit.plugininfo.controller.PluginController;
import org.petricek.bukkit.plugininfo.model.ExportType;

/**
 *
 * @author Michal Petříček
 */
public class PluginInfo extends JavaPlugin {

    private String name;
    private String version;

    public static ApiCraftController apiCraftController;
    public static ExportController exportController;
    public static PluginController pluginController;
    public static Commands commands;
    public static Settings settings;
    public static SettingsXml settingsXml;
    public static SettingsTxt settingsTxt;

    public static final ChatColor nameColor = ChatColor.YELLOW;
    public static final ChatColor versionColor = ChatColor.AQUA;
    public static final ChatColor textColor = ChatColor.WHITE;
    public static final ChatColor headertColor = ChatColor.DARK_GREEN;
    public static final ChatColor commandColor = ChatColor.YELLOW;
    public static final ChatColor errorColor = ChatColor.RED;

    public void onDisable() {
        BukkitLogger.info(name + " " + version + " disabled");
    }

    public void onEnable() {
        name = this.getDescription().getName();
        version = this.getDescription().getVersion();

        settings = new Settings(this.getDataFolder());
        settingsXml = new SettingsXml(this.getDataFolder());
        settingsTxt = new SettingsTxt(this.getDataFolder());

        pluginController = new PluginController(this.getServer());
        exportController = new ExportController(this.getServer());
        apiCraftController = new ApiCraftController(this.getServer(), this);
        
        commands = new Commands(this.getServer(), exportController, pluginController, apiCraftController);

        registerHelp();

        BukkitLogger.info(name + " " + version + " enabled");
        
        exportController.export(ExportType.DEFAULT, pluginController.getPluginVersionsList(), pluginController.getServerInfo());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String commandName = command.getName().toLowerCase();

        if (commandName.equals("plugi") || commandName.equals("plugininfo")) {
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("l")) {
                    commands.getPlugins(sender, 1);
                } else if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("r")) {
                    commands.reload(sender);
                } else if (args[0].equalsIgnoreCase("export") || args[0].equalsIgnoreCase("e")) {
                    commands.export(sender, null);
                } else if (args[0].equalsIgnoreCase("upload") || args[0].equalsIgnoreCase("u")) {
                    commands.upload(sender, ExportType.DEFAULT);
                } else if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("h") || args[0].equalsIgnoreCase("?")) {
                    commands.help(sender);
                } else {
                    commands.help(sender);
                }
            } else if (args.length == 2) {
                if ((args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("l")) && isInteger(args[1])) {
                    commands.getPlugins(sender, Integer.parseInt(args[1]));
                } else if ((args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("l")) && args[1].equalsIgnoreCase("all")) {
                    commands.getAllPlugins(sender);
                } else if (args[0].equalsIgnoreCase("export") || args[0].equalsIgnoreCase("e")) {
                    commands.export(sender, args[1]);
                } else if ((args[0].equalsIgnoreCase("upload") || args[0].equalsIgnoreCase("u"))) {
                    commands.upload(sender, args[1]);
                } else {
                    commands.help(sender);
                }
            } else {
                commands.help(sender);
            }

            return true;
        }

        return false;
    }

    public void registerHelp() {
        Plugin p = this.getServer().getPluginManager().getPlugin("Help");
        if (p != null) {
            Help helpPlugin = ((Help) p);
            helpPlugin.registerCommand("help PluginInfo", "Displays PluginInfo help", this, true);
            helpPlugin.registerCommand("plugi [help|h|?]", "Displays integrated PluginInfo help", this);
            helpPlugin.registerCommand("plugi list|l [#]", "Displays formatted list of plugins and their versions, page number [#]", this, Permissions.PERMISSION_VIEW);
            helpPlugin.registerCommand("plugi list|l all", "Displays versions of all plugins", this, Permissions.PERMISSION_VIEW);
            helpPlugin.registerCommand("plugi export|e", "Exports info about plugins to file types defined in config.yml", this, Permissions.PERMISSION_EXPORT);
            helpPlugin.registerCommand("plugi export|e list|l", "List of available export types", this, Permissions.PERMISSION_EXPORT);
            helpPlugin.registerCommand("plugi export|e [param]", "Exports info about plugins to [param]-type file", this, Permissions.PERMISSION_EXPORT_ALL);
            helpPlugin.registerCommand("plugi export|e all", "Exports info about plugins to all available file types", this, Permissions.PERMISSION_EXPORT_ALL);
            helpPlugin.registerCommand("plugi upload|u", "Uploads all available exported files defined in settings.yml to ftp (if enabled)", this, Permissions.PERMISSION_UPLOAD);
            helpPlugin.registerCommand("plugi upload|u all", "Uploads all available exported files to ftp (if enabled)", this, Permissions.PERMISSION_UPLOAD);
            helpPlugin.registerCommand("plugi upload|u [param]", "Uploads specified exported file to ftp (if enabled)", this, Permissions.PERMISSION_UPLOAD);
            helpPlugin.registerCommand("plugi upload|u list", "List of available files to upload", this, Permissions.PERMISSION_UPLOAD);
            helpPlugin.registerCommand("plugi reload|r", "Reloads settings", this, Permissions.PERMISSION_RELOAD);
            BukkitLogger.info("'Help' support enabled.");
        } else {
            BukkitLogger.info("'Help' isn't detected. No /help support.");
        }
    }

    public static boolean isInteger(String string) {
        try {
            Integer.parseInt(string);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
