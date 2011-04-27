package org.petricek.bukkit.pluginversion;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Michal Petříček
 */
public class ServerInfo {

    private static final Pattern BUKKIT_BUILD_PATTERN = Pattern.compile("b[0-9]+jnks");
    private static final Pattern MC_VERSION_PATTERN = Pattern.compile("\\(MC: [0-9\\._]+\\)");

    private String bukkitName;
    private String bukkitVersion;
    private String minecraftVersion;
    private String serverName;
    private String serverPort;

    public void setVersions(String version) {
        bukkitVersion = parseBukkitBuild(version);
        minecraftVersion = parseMCVersion(version);
        bukkitName = version;
    }

    public String getBukkitName() {
        return bukkitName;
    }

    public void setBukkitName(String bukkitName) {
        this.bukkitName = bukkitName;
    }

    public String getBukkitVersion() {
        return bukkitVersion;
    }

    public void setBukkitVersion(String bukkitVersion) {
        this.bukkitVersion = bukkitVersion;
    }

    public String getMinecraftVersion() {
        return minecraftVersion;
    }

    public void setMinecraftVersion(String minecraftVersion) {
        this.minecraftVersion = minecraftVersion;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getServerPort() {
        return serverPort;
    }

    public void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }

    private static String parseBukkitBuild(String version){
        final Matcher matcher = BUKKIT_BUILD_PATTERN.matcher(version);
        if(matcher.find()){
            return matcher.group().substring(1, matcher.group().length() - 4);
        }else{
            return version;
        }
    }

    private static String parseMCVersion(String version){
        final Matcher matcher = MC_VERSION_PATTERN.matcher(version);
        if(matcher.find()){
            return matcher.group().substring(5, matcher.group().length() - 1);
        }else{
            return version;
        }
    }

}


