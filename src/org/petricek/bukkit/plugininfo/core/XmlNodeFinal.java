package org.petricek.bukkit.plugininfo.core;

import java.io.Serializable;

/**
 *
 * @author Michal Petříček
 */
public class XmlNodeFinal extends XmlNode implements Serializable {

    private String node;    //String, XmlNode

    public XmlNodeFinal(String name, String node) {
        this.name = normalizeXml(name);
        this.node = normalizeXml(node);
    }

    public void insertElement(XmlNode node) {
        throw new IllegalStateException("Should not invoke insertElement() on final list of XML tree");
    }

    public Object getNode() {
        return node;
    }

}
