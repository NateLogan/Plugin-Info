package org.petricek.bukkit.plugininfo.utils;

import java.util.Calendar;

/**
 *
 * @author Michal Petříček
 */
public class Utils {
    
    public static String getDateStamp() {
        Calendar cal = Calendar.getInstance();
        String out = "";
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        out += year + "-";
        out += (month < 10 ? "0" + month : month) + "-";
        out += (day < 10 ? "0" + day : day);

        return out;
    }

    public static String getTimeStamp() {
        Calendar cal = Calendar.getInstance();
        String out = "";
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);
        int sec = cal.get(Calendar.SECOND);

        out += (hour < 10 ? "0" + hour : hour) + ":";
        out += (min < 10 ? "0" + min : min) + ":";
        out += (sec < 10 ? "0" + sec : sec);

        return out;
    }
    
    public static boolean isInteger(String string) {
        try {
            Integer.parseInt(string);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

}
