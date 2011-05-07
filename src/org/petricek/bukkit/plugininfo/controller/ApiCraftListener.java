package org.petricek.bukkit.plugininfo.controller;

import java.util.HashMap;
import org.kokakiwi.apicraft.events.ApiEvent;
import org.kokakiwi.apicraft.events.ApiListener;
import org.petricek.bukkit.plugininfo.BukkitLogger;

/**
 *
 * @author Michal Petříček
 */
public class ApiCraftListener extends ApiListener {

    private Object xml = new HashMap();
    private boolean enabled = true;

    public void setXml(Object xml) {
        this.xml = xml;
        BukkitLogger.info("ApiCraft data updated");
    }

    public void enable() {
        enabled = true;
    }

    public void disable() {
        enabled = false;
    }

    @Override
    public void onApiEvent(ApiEvent event) {
        if (!enabled) {
            return;
        }

        try {
            if (event.path[0].equalsIgnoreCase("plugininfo")) {
                event.setFormat("xml");
                event.setResponse(xml);
                event.setActionTaken(true);
            }
        } catch (Exception e) {
            BukkitLogger.severe("Error while ApiCraft event handling", e);
        }
    }
}
