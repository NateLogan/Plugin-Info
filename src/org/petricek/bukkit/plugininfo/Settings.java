package org.petricek.bukkit.plugininfo;

import java.io.File;
import org.petricek.bukkit.plugininfo.util.BetterConfig;

/**
 *
 * @author Michal Petříček
 */
public class Settings {

    private final String settingsFile = "settings.yml";
    private final File dataFolder;
    private BetterConfig config;

    public int entriesPerPage;
    public String outputFolder;
    public boolean ftpEnabled;
    public boolean ftpAutoUpload;
    public String ftpPath;
    public String ftpUsername;
    public String ftpPasswd;
    public String ftpServer;
    
    public String xmlFileName;
    public String xmlOutputFolder;
    public boolean xmlSaveEnabled;

    /*public String htmlFileName;
    public String htmlOutputFolder;
    public boolean htmlSaveEnabled;*/

    public String txtFileName;
    public String txtOutputFolder;
    public boolean txtSaveEnabled;

    public Settings(File dataFolder) {
        this.dataFolder = dataFolder;
        initialize();
    }

    public final void initialize() {
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        File configFile = new File(dataFolder, settingsFile);
        config = new BetterConfig(configFile);
        config.load();

        entriesPerPage = config.getInt("entriesPerPage", 9);
        outputFolder = config.getString("outputFolder", dataFolder.getPath());

        ftpEnabled = config.getBoolean("ftpEnabled", false);
        ftpAutoUpload = config.getBoolean("ftpAutoUpload", false);
        ftpUsername = config.getString("ftpUsername", "username");
        ftpPasswd = config.getString("ftpPasswd", "password");
        ftpServer = config.getString("ftpServer", "server-address.com");
        ftpPath = config.getString("ftpPath", "");

        xmlFileName = config.getString("xmlFileName", "plugins.xml");
        xmlOutputFolder = config.getString("xmlOutputFolder", "");
        xmlSaveEnabled = config.getBoolean("xmlSaveEnabled", false);

        /*
        htmlFileName = config.getString("htmlFileName", "plugins.html");
        htmlOutputFolder = config.getString("htmlOutputFolder", "");
        htmlSaveEnabled = config.getBoolean("htmlSaveEnabled", false);
        */
        
        txtFileName = config.getString("txtFileName", "plugins.txt");
        txtOutputFolder = config.getString("txtOutputFolder", "");
        txtSaveEnabled = config.getBoolean("txtSaveEnabled", false);

        save();
    }

    public String getTxtOutputFile(){
        if(txtOutputFolder == null || txtOutputFolder.isEmpty()){
            return (new File(outputFolder, txtFileName)).getPath();
        } else {
            return (new File(txtOutputFolder, txtFileName)).getPath();
        }
    }

    public String getXmlOutputFile(){
        if(xmlOutputFolder == null || xmlOutputFolder.isEmpty()){
            return (new File(outputFolder, xmlFileName)).getPath();
        } else {
            return (new File(xmlOutputFolder, xmlFileName)).getPath();
        }
    }

    public boolean save(){
        return config.save();
    }
}
