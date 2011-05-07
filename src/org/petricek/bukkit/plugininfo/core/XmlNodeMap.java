/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.petricek.bukkit.plugininfo.core;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Michal Petříček
 */
public class XmlNodeMap extends XmlNode implements Serializable {

    ArrayList<XmlNode> node = new ArrayList<XmlNode>();
    
    public XmlNodeMap(String name) {
        this.name = normalizeXml(name);
    }

    public XmlNodeMap(String name, XmlNode node) {
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
