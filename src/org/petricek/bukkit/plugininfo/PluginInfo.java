package org.petricek.bukkit.plugininfo;

import java.util.ArrayList;
import java.util.Collections;
import me.taylorkelly.help.Help;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Michal Petříček
 */
public class PluginInfo extends JavaPlugin {

    private String name;
    private String version;

    public static Commands commands;
    public static Settings settings;
    public static SettingsXml settingsXml;
    public static SettingsTxt settingsTxt;

    public void onDisable() {
        BukkitLogger.info(name + " " + version + " disabled");
    }

    public void onEnable() {
        name = this.getDescription().getName();
        version = this.getDescription().getVersion();

        settings = new Settings(this.getDataFolder());
        settingsXml = new SettingsXml(this.getDataFolder());
        settingsTxt = new SettingsTxt(this.getDataFolder());

        commands = new Commands(this.getServer());

        registerHelp();

        BukkitLogger.info(name + " " + version + " enabled");
        DataExport.onEnableSave(getPluginVersionsList(), getServerInfo());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String commandName = command.getName().toLowerCase();

        if (commandName.equals("plugi") || commandName.equals("plugininfo")) {
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("l")) {
                    commands.getPlugins(sender, getPluginVersionsList(), 1);
                } else if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("r")) {
                    commands.reload(sender);
                } else if (args[0].equalsIgnoreCase("export") || args[0].equalsIgnoreCase("e")) {
                    commands.export(sender, getPluginVersionsList(), getServerInfo(), null);
                } else if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("h") || args[0].equalsIgnoreCase("?")) {
                    commands.help(sender);
                } else {
                    commands.help(sender);
                }
            } else if (args.length == 2) {
                if ((args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("l")) && isInteger(args[1])) {
                    commands.getPlugins(sender, getPluginVersionsList(), Integer.parseInt(args[1]));
                } else if ((args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("l")) && args[1].equalsIgnoreCase("all")) {
                    commands.getAllPlugins(sender, getPluginVersionsList());
                } else if (args[0].equalsIgnoreCase("export") || args[0].equalsIgnoreCase("e")) {
                    commands.export(sender, getPluginVersionsList(), getServerInfo(), args[1]);
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

    private ArrayList<PluginData> getPluginVersionsList() {
        Plugin[] plugins = this.getServer().getPluginManager().getPlugins();
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
        serverInfo.setVersions(this.getServer().getVersion());
        serverInfo.setServerName(this.getServer().getServerName());
        serverInfo.setServerPort("" + this.getServer().getPort());
        return serverInfo;
    }

    public void registerHelp() {
        Plugin p = this.getServer().getPluginManager().getPlugin("Help");
        if (p != null) {
            Help helpPlugin = ((Help) p);
            helpPlugin.registerCommand("help plugi", "Displays help", this, true);
            helpPlugin.registerCommand("plugi [help|h|?]", "Displays integrated help", this);
            helpPlugin.registerCommand("plugi list|l [#]", "Displays formatted list of plugins and their versions, page number [#]", this, Permissions.PERMISSION_VIEW);
            helpPlugin.registerCommand("plugi list|l all", "Displays versions of all plugins", this, Permissions.PERMISSION_VIEW);
            helpPlugin.registerCommand("plugi export|e", "Exports info about plugins to file types defined in config.yml", this, Permissions.PERMISSION_EXPORT);
            helpPlugin.registerCommand("plugi export|e list|l", "List of available export types", this, Permissions.PERMISSION_EXPORT);
            helpPlugin.registerCommand("plugi export|e [param]", "Exports info about plugins to [param]-type file", this, Permissions.PERMISSION_EXPORT_ALL);
            helpPlugin.registerCommand("plugi export|e all", "Exports info about plugins to all available file types", this, Permissions.PERMISSION_EXPORT_ALL);
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
