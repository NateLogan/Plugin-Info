package org.petricek.bukkit.plugininfo.core;

import java.io.Serializable;

/**
 *
 * @author Michal Petříček
 */
public abstract class XmlNode implements Serializable {

    protected String name = null;
    
    public abstract void insertElement(XmlNode node);

    public abstract Object getNode();

    public String getName() {
        return name;
    }

    protected static String normalizeXml(String s) {
        if (s == null) return s;
        while (s.endsWith("\r") || s.endsWith("\n")) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }
}
