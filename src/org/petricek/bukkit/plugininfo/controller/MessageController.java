package org.petricek.bukkit.plugininfo.controller;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.petricek.bukkit.plugininfo.BukkitLogger;

/**
 *
 * @author Michal Petříček
 */
public class MessageController {

    public static void sendMessage(CommandSender sender, String message) {
        if (sender == null) {
            BukkitLogger.info(message);
        } else if (sender instanceof Player) {
            sender.sendMessage(message);
        } else {
            sender.sendMessage("[PluginInfo] " + message);
        }
    }
}
