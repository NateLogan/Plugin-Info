package org.petricek.bukkit.plugininfo.core;

import java.io.Serializable;
import java.util.LinkedList;

/**
 *
 * @author Michal Petříček
 */
public class XmlNodeList extends XmlNode implements Serializable {

    private LinkedList<XmlNode> node = new LinkedList<XmlNode>();

    public XmlNodeList(String name) {
        this.name = normalizeXml(name);
    }

    public XmlNodeList(String name, XmlNode node) {
        this.name = normalizeXml(name);
        this.node.add(node);
    }

    @Override
    public void insertElement(XmlNode node) {
       this.node.add(node);
    }

    @Override
    public Object getNode() {
        return node;
    }

}
