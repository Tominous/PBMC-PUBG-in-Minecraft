package net.aluix.pubg.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class UtilTime
{
    public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_DAY = "yyyy-MM-dd";
    
    public static String now() {
        final Calendar cal = Calendar.getInstance();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(cal.getTime());
    }
    
    public static String when(final long time) {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(time);
    }
    
    public static String date() {
        final Calendar cal = Calendar.getInstance();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(cal.getTime());
    }
    
    public static String since(final long epoch) {
        return "Took " + convertString(System.currentTimeMillis() - epoch, 1, TimeUnit.FIT) + ".";
    }
    
    public static double convert(final long time, final int trim, TimeUnit type) {
        if (type == TimeUnit.FIT) {
            if (time < 60000L) {
                type = TimeUnit.SECONDS;
            }
            else if (time < 3600000L) {
                type = TimeUnit.MINUTES;
            }
            else if (time < 86400000L) {
                type = TimeUnit.HOURS;
            }
            else {
                type = TimeUnit.DAYS;
            }
        }
        if (type == TimeUnit.DAYS) {
            return UtilMath.trim(trim, time / 8.64E7);
        }
        if (type == TimeUnit.HOURS) {
            return UtilMath.trim(trim, time / 3600000.0);
        }
        if (type == TimeUnit.MINUTES) {
            return UtilMath.trim(trim, time / 60000.0);
        }
        if (type == TimeUnit.SECONDS) {
            return UtilMath.trim(trim, time / 1000.0);
        }
        return UtilMath.trim(trim, (double)time);
    }
    
    public static String MakeStr(final long time) {
        return convertString(time, 1, TimeUnit.FIT);
    }
    
    public static String MakeStr(final long time, final int trim) {
        return convertString(time, trim, TimeUnit.FIT);
    }
    
    public static String convertString(final long time, final int trim, TimeUnit type) {
        if (time == -1L) {
            return "Permanent";
        }
        if (type == TimeUnit.FIT) {
            if (time < 60000L) {
                type = TimeUnit.SECONDS;
            }
            else if (time < 3600000L) {
                type = TimeUnit.MINUTES;
            }
            else if (time < 86400000L) {
                type = TimeUnit.HOURS;
            }
            else {
                type = TimeUnit.DAYS;
            }
        }
        if (type == TimeUnit.DAYS) {
            return String.valueOf(String.valueOf(UtilMath.trim(trim, time / 8.64E7))) + " Days";
        }
        if (type == TimeUnit.HOURS) {
            return String.valueOf(String.valueOf(UtilMath.trim(trim, time / 3600000.0))) + " Hours";
        }
        if (type == TimeUnit.MINUTES) {
            return String.valueOf(String.valueOf(UtilMath.trim(trim, time / 60000.0))) + " Minutes";
        }
        if (type == TimeUnit.SECONDS) {
            return String.valueOf(String.valueOf(UtilMath.trim(trim, time / 1000.0))) + " Seconds";
        }
        return String.valueOf(String.valueOf(UtilMath.trim(trim, (double)time))) + " Milliseconds";
    }
    
    public static boolean elapsed(final long from, final long required) {
        return System.currentTimeMillis() - from > required;
    }
    
    public enum TimeUnit
    {
        FIT("FIT", 0, "FIT", 0), 
        DAYS("DAYS", 1, "DAYS", 1), 
        HOURS("HOURS", 2, "HOURS", 2), 
        MINUTES("MINUTES", 3, "MINUTES", 3), 
        SECONDS("SECONDS", 4, "SECONDS", 4), 
        MILLISECONDS("MILLISECONDS", 5, "MILLISECONDS", 5);
        
        private TimeUnit(final String s2, final int n2, final String s, final int n) {
        }
    }
}
