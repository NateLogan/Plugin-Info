package org.petricek.bukkit.pluginversion;

/**
 *
 * @author Michal Petříček
 */
public class PluginInfo implements Comparable<PluginInfo> {

    public PluginInfo() {
    }

    private String name;
    private String description;
    private String version;
    private String website;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public int compareTo(PluginInfo o) {
        return this.name.compareToIgnoreCase(o.name);
    }
    

}
