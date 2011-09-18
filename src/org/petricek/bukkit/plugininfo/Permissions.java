package org.petricek.bukkit.plugininfo;

import com.nijiko.permissions.PermissionHandler;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Class for handling permissions.
 * @author Michal Petříček
 */
public class Permissions {

    public static final String PERMISSION_VIEW = "plugininfo.view";
    public static final String PERMISSION_EXPORT = "plugininfo.export";
    public static final String PERMISSION_EXPORT_ALL = "plugininfo.export.all";
    public static final String PERMISSION_RELOAD = "plugininfo.reload";
    public static final String PERMISSION_EDIT = "plugininfo.edit";
    public static final String PERMISSION_UPLOAD = "plugininfo.upload";
    
    private final PermissionsEntity permissions;

    public Permissions(Server server) {
        Plugin groupManager = server.getPluginManager().getPlugin("GroupManager");
        Plugin permissionsPlugin = server.getPluginManager().getPlugin("Permissions");
        Plugin permissionsBukkit = server.getPluginManager().getPlugin("PermissionsBukkit");
        if (permissionsBukkit != null) {
            BukkitLogger.info("PermissionsBukkit plugin detected.");
            permissions = new PermissionsBukkit();
        } else if (permissionsPlugin != null) {
            BukkitLogger.info("Permissions plugin detected.");
            permissions = new PermissionsP(((com.nijikokun.bukkit.Permissions.Permissions) permissionsPlugin));
        } else {
            BukkitLogger.info("No permissions plugin detected, defaulting to OP.");
            permissions = new PermissionsBukkit();
        }
    }

    public boolean checkPermission(Player player, String permissionNode) {
        if (player == null) {
            return true;
        }
        return permissions.checkPermission(player, permissionNode);
    }

    private interface PermissionsEntity {

        public boolean checkPermission(Player player, String permissionNode);
    }

    private class PermissionsP implements PermissionsEntity {

        private final PermissionHandler permissionHandler;

        public PermissionsP(com.nijikokun.bukkit.Permissions.Permissions permissions) {
            this.permissionHandler = permissions.getHandler();
        }

        public boolean checkPermission(Player player, String permissionNode) {
            return permissionHandler.has(player, permissionNode);
        }
    }

    private class PermissionsBukkit implements PermissionsEntity {

        public boolean checkPermission(Player player, String permissionNode) {
            return player.hasPermission(permissionNode);
        }
    }
}
