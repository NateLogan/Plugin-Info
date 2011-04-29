package org.petricek.bukkit.plugininfo;

import com.nijiko.permissions.PermissionHandler;
import org.anjocaido.groupmanager.GroupManager;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author Michal Petříček
 */
public class Permissions {

    public static final String PERMISSION_VIEW = "plugininfo.view";
    public static final String PERMISSION_EXPORT = "plugininfo.export";
    public static final String PERMISSION_EXPORT_ALL = "plugininfo.export.all";
    public static final String PERMISSION_RELOAD = "plugininfo.reload";
    public static final String PERMISSION_EDIT = "plugininfo.edit";

    private final PermissionsEntity permissions;

    public Permissions(Server server) {
        Plugin groupManager = server.getPluginManager().getPlugin("GroupManager");
        Plugin permissionsPlugin = server.getPluginManager().getPlugin("Permissions");
        if (groupManager != null) {
            BukkitLogger.info("GroupManager plugin detected.");
            if (!server.getPluginManager().isPluginEnabled(groupManager)) {
                server.getPluginManager().enablePlugin(groupManager);
            }
            permissions = new PermissionsGM((GroupManager) groupManager);
        } else if (permissionsPlugin != null) {
            BukkitLogger.info("Permissions plugin detected.");
            permissions = new PermissionsP(((com.nijikokun.bukkit.Permissions.Permissions) permissionsPlugin));
        } else {
            BukkitLogger.info("No permissions plugin detected, defaulting to OP.");
            permissions = new PermissionsOP();
        }
    }

    public boolean checkPermission(Player player, String permissionNode){
        if(player == null) return true;
        return permissions.checkPermission(player, permissionNode);
    }

    private interface PermissionsEntity {
        public boolean checkPermission(Player player, String permissionNode);
    }

    private class PermissionsGM implements PermissionsEntity {
        private final GroupManager gm;

        public PermissionsGM(GroupManager gm) {
            this.gm = gm;
        }

        public boolean checkPermission(Player player, String permissionNode) {
            return gm.getWorldsHolder().getWorldPermissions(player).has(player, permissionNode);
        }

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

    private class PermissionsOP implements PermissionsEntity {

        public boolean checkPermission(Player player, String permissionNode) {
            if(permissionNode.equalsIgnoreCase(PERMISSION_VIEW)){
                return true;
            }
            return player.isOp();
        }

    }
}
