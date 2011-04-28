package org.petricek.bukkit.plugininfo;

import java.util.ArrayList;

/**
 *
 * @author Michal Petříček
 */
public class PluginData implements Comparable<PluginData> {

    public PluginData() {
    }

    private String name;
    private String fullName;
    private String description;
    private String version;
    private String website;
    private ArrayList<String> authors;
    private boolean databaseEnabled;
    private Object commands;
    private Object depend;
    private boolean enabled;

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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public ArrayList<String> getAuthors() {
        return authors;
    }

    public void setAuthors(ArrayList<String> authors) {
        this.authors = authors;
    }

    public boolean isDatabaseEnabled() {
        return databaseEnabled;
    }

    public void setDatabaseEnabled(boolean databaseEnabled) {
        this.databaseEnabled = databaseEnabled;
    }

    public Object getCommands() {
        return commands;
    }

    public void setCommands(Object commands) {
        this.commands = commands;
    }

    public Object getDepend() {
        return depend;
    }

    public void setDepend(Object depend) {
        this.depend = depend;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int compareTo(PluginData o) {
        return this.name.compareToIgnoreCase(o.name);
    }
    

}
