package org.petricek.bukkit.pluginversion;

import com.jascotty2.JMinecraftFontWidthCalculator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author Michal Petříček
 */
public class PluginVersion extends JavaPlugin {

    private String name;
    private String version;
    private final ChatColor nameColor = ChatColor.YELLOW;
    private final ChatColor versionColor = ChatColor.AQUA;
    private final ChatColor textColor = ChatColor.WHITE;
    private final ChatColor headertColor = ChatColor.DARK_GREEN;
    private final ChatColor commandColor = ChatColor.YELLOW;
    private final ChatColor errorColor = ChatColor.RED;

    public void onDisable() {
        BukkitLogger.info(name + " " + version + " disabled");
    }

    public void onEnable() {
        name = this.getDescription().getName();
        version = this.getDescription().getVersion();

        PluginSettings.initialize(this.getDataFolder());

        BukkitLogger.info(name + " " + version + " enabled");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String commandName = command.getName().toLowerCase();

        if (commandName.equals("pluginversion")) {
            LinkedList<String> messages = new LinkedList<String>();
            boolean console = !(sender instanceof Player);

            if (args.length == 1) {
                if (isInteger(args[0])) {
                    int page = Integer.parseInt(args[0]);
                    messages = getPage(page, console);
                } else if (args[0].equalsIgnoreCase("all")) {
                    messages = getAll(console);
                } else if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) {
                    messages = getHelp(console);
                }
            } else {
                messages = getHelp(console);
            }

            for (String message : messages) {
                sender.sendMessage(message);
            }

            return true;
        }

        return false;
    }

    private LinkedList<String> getHelp(boolean console) {
        LinkedList<String> list = new LinkedList<String>();
        list.add(headertColor.toString() + "[Plugin Version HELP]");
        list.add(commandColor.toString() + " /pluginversion all" + textColor.toString() + " - List all plugins");
        list.add(commandColor.toString() + " /pluginversion #" + textColor.toString() + " - Detailed list of " + PluginSettings.entriesPerPage + " plugins at a time");
        return list;
    }

    private LinkedList<String> getAll(boolean console) {
        ArrayList<PluginInfo> versionList = getVersionsList();
        LinkedList<String> list = new LinkedList<String>();
        StringBuilder out = new StringBuilder();

        for (PluginInfo pluginInfo : versionList) {
            String t = nameColor.toString() + pluginInfo.getName() + " " + versionColor.toString() + pluginInfo.getVersion() + textColor.toString() + "; ";
            out.append(t);
        }

        list.add(out.substring(0, out.length() - 2));
        return list;
    }

    private LinkedList<String> getPage(int page, boolean console) {
        ArrayList<PluginInfo> versionList = getVersionsList();
        LinkedList<String> list = new LinkedList<String>();

        if (page < 1 || page > versionList.size() / PluginSettings.entriesPerPage + 1) {
            list.add(errorColor.toString() + "Page " + page + " does not exist");
        } else {
            list.add(headertColor.toString() + "[Plugin Versions HELP " + nameColor.toString() + (page) + "/" + (versionList.size() / PluginSettings.entriesPerPage + 1) + headertColor.toString() + "]");
            page -= 1;
            final int maxNameLength = getMaxNameLength(versionList);
            final int longestNameWidth = getLongestNameWidth(versionList);
            for (int i = page * PluginSettings.entriesPerPage; i < page * PluginSettings.entriesPerPage + PluginSettings.entriesPerPage && i < versionList.size(); i++) {
                list.add(console ? getLineConsole(versionList.get(i), maxNameLength) : getLineGame(versionList.get(i), longestNameWidth));
            }
        }

        return list;
    }

    private ArrayList<PluginInfo> getVersionsList() {
        Plugin[] plugins = this.getServer().getPluginManager().getPlugins();
        ArrayList<PluginInfo> list = new ArrayList<PluginInfo>(plugins.length);

        for (Plugin plugin : plugins) {
            PluginInfo pluginInfo = new PluginInfo();
            pluginInfo.setName(plugin.getDescription().getName());
            pluginInfo.setDescription(plugin.getDescription().getDescription());
            pluginInfo.setVersion(plugin.getDescription().getVersion());
            pluginInfo.setWebsite(plugin.getDescription().getWebsite());
            list.add(pluginInfo);
        }

        Collections.sort(list);

        return list;
    }
    
    private String getLineConsole(PluginInfo pluginInfo, int maxNameLength) {
        StringBuilder out = new StringBuilder(nameColor.toString() + pluginInfo.getName());
        for (int i = out.length(); i < maxNameLength; i++) {
            out.append(" ");
        }
        out.append(textColor.toString()).append(" - ");
        out.append(versionColor.toString()).append(pluginInfo.getVersion());
        return out.toString();
    }

    private String getLineGame(PluginInfo pluginInfo, int maxNameWidth) {
        StringBuilder out = new StringBuilder(nameColor.toString() + pluginInfo.getName());
        for (int i = JMinecraftFontWidthCalculator.getStringWidth(pluginInfo.getName()); i < maxNameWidth; i += JMinecraftFontWidthCalculator.getCharWidth(' ')) {
            out.append(' ');
        }
        out.append(textColor.toString()).append(" - ");
        out.append(versionColor.toString()).append(pluginInfo.getVersion());
        return out.toString();
    }

    private int getMaxNameLength(ArrayList<PluginInfo> list) {
        int maxLength = 0;
        for (PluginInfo pluginInfo : list) {
            if(pluginInfo.getName().length() > maxLength) {
                maxLength = pluginInfo.getName().length();
            }
        }
        return maxLength;
    }

    private int getLongestNameWidth(ArrayList<PluginInfo> list) {
        int maxWidth = 0;
        for (PluginInfo pluginInfo : list) {
            int stringWidth = JMinecraftFontWidthCalculator.getStringWidth(pluginInfo.getName());
            if(stringWidth > maxWidth) {
                maxWidth = stringWidth;
            }
        }
        return maxWidth;
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
