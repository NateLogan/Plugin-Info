/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.petricek.bukkit.pluginversion;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.URL;

/**
 *
 * @author Michal Petříček
 */
public class GetExternalIp {

    private GetExternalIp() {
    }

    public static InetAddress getExternalIp() {
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        InetAddress ip = null;

        try {
            URL url = new URL("http://www.whatismyip.com/automation/n09230945.asp");
            inputStream = url.openStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            ip = Inet4Address.getByName(bufferedReader.readLine());
        } catch (Exception ex) {
            //Something bad happened, return null
        } finally {
            try {
                inputStream.close();
            } catch (Exception ex) {
            }

            try {
                bufferedReader.close();
            } catch (Exception ex) {
            }

        }

        return ip;
    }

    public static void main(String[] args) {
        System.out.println(getExternalIp());
    }
}
