package org.petricek.bukkit.plugininfo.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.petricek.bukkit.plugininfo.BukkitLogger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.petricek.bukkit.plugininfo.core.XmlNode;
import org.petricek.bukkit.plugininfo.core.XmlNodeList;
import org.petricek.bukkit.plugininfo.core.XmlNodeFinal;
import org.petricek.bukkit.plugininfo.core.XmlNodeMap;
import org.w3c.dom.Node;

/**
 *
 * @author Michal Petříček
 */
public class XmlDocument {

    private XmlNode root;

    public XmlDocument(XmlNode root) {
        this.root = root;
    }

    public Object getApiCraftXml() {
        return getApiCraftElementStructure(root);
    }

    private Object getApiCraftElementStructure(XmlNode node) {
        if (node == null) {
            return "Nothing to export";
        } else if (node instanceof XmlNodeFinal) {
            return normalize(((XmlNode) node).getNode().toString());
        } else if (node instanceof XmlNodeList) {
            LinkedList list = new LinkedList();
            Iterator it = ((List) node.getNode()).iterator();
            while (it.hasNext()) {
                list.add(getApiCraftElementStructure((XmlNode) it.next()));
            }
            return list;
        } else if (node instanceof XmlNodeMap) {
            HashMap map = new HashMap();
            Iterator it = ((List) node.getNode()).iterator();
            while (it.hasNext()) {
                XmlNode itNode = (XmlNode) it.next();
                map.put(itNode.getName(), getApiCraftElementStructure(itNode));
            }
            return map;
        } else {
            throw new IllegalStateException("Internal Exception (XML transformation for ApiCraft)");
        }
    }

    public Document getXmlDoc() {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document doc = docBuilder.newDocument();

            return getXmlDocElementStructure(root, doc, doc);
        } catch (ParserConfigurationException ex) {
            BukkitLogger.severe("Exception while parsing plugin data", ex);
        } catch (Exception ex) {
            BukkitLogger.severe("Unknown Exception while parsing plugin data", ex);
        }
        return null;
    }

    private Document getXmlDocElementStructure(XmlNode node, Node parent, Document doc) {
        if (node == null) {
            return doc;
        } else if (node instanceof XmlNodeFinal) {
            Element e = doc.createElement(node.getName());
            e.appendChild(doc.createTextNode((String) node.getNode()));
            parent.appendChild(e);
        } else if (node instanceof XmlNodeList || node instanceof XmlNodeMap) {
            Iterator it = ((List) node.getNode()).iterator();
            Element e = doc.createElement(node.getName());
            parent.appendChild(e);
            while (it.hasNext()) {
                getXmlDocElementStructure((XmlNode) it.next(), e, doc);
            }
        } else {
            throw new IllegalStateException("Internal Exception (XML transformation for ApiCraft)");
        }
        return doc;
    }
    
    private String normalize(String s){
        s = s.replace("&", "&amp;");
        s = s.replace("<", "&lt;");
        s = s.replace(">", "&gt;");
        return s;
    }
}
