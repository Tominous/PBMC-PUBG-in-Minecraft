package net.aluix.pubg.game.timer;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import net.aluix.pubg.utils.UtilMath;

public class TimeHelper
{
    private long lastMS;
    private long currentMS;
    public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_DAY = "yyyy-MM-dd";
    
    public long getCurrentMS() {
        return System.nanoTime() / 1000000L;
    }
    
    public long getLastMS() {
        return this.lastMS;
    }
    
    public boolean hasReached(final long milliseconds) {
        return this.getCurrentMS() - this.lastMS >= milliseconds;
    }
    
    public void reset() {
        this.lastMS = this.getCurrentMS();
    }
    
    public void setLastMS(final long currentMS) {
        this.lastMS = currentMS;
    }
    
    public final void updateMS() {
        this.currentMS = System.currentTimeMillis();
    }
    
    public final void updateLastMS() {
        this.lastMS = System.currentTimeMillis();
    }
    
    public final boolean hasTimePassedM(final long MS) {
        return this.currentMS >= this.lastMS + MS;
    }
    
    public final boolean hasTimePassedS(final float speed) {
        return this.currentMS >= this.lastMS + (long)(1000.0f / speed);
    }
    
    public static String now() {
        final Calendar cal = Calendar.getInstance();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(cal.getTime());
    }
    
    public static long nowlong() {
        return System.currentTimeMillis();
    }
    
    public static String when(final long time) {
        final SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        return sdf.format(time);
    }
    
    public static long a(final String a) {
        if (a.endsWith("s")) {
            return Long.valueOf(a.substring(0, a.length() - 1)) * 1000L;
        }
        if (a.endsWith("m")) {
            return Long.valueOf(a.substring(0, a.length() - 1)) * 60000L;
        }
        if (a.endsWith("h")) {
            return Long.valueOf(a.substring(0, a.length() - 1)) * 3600000L;
        }
        if (a.endsWith("d")) {
            return Long.valueOf(a.substring(0, a.length() - 1)) * 86400000L;
        }
        if (a.endsWith("m")) {
            return Long.valueOf(a.substring(0, a.length() - 1)) * 2592000000L;
        }
        if (a.endsWith("y")) {
            return Long.valueOf(a.substring(0, a.length() - 1)) * 31104000000L;
        }
        return -1L;
    }
    
    public static String date() {
        final Calendar cal = Calendar.getInstance();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(cal.getTime());
    }
    
    public static String getTime(final int time) {
        final Date timeDiff = new Date();
        timeDiff.setTime(time * 1000);
        final SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss");
        final String eventTimeDisplay = timeFormat.format(timeDiff);
        return eventTimeDisplay;
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
            return String.valueOf(UtilMath.trim(trim, time / 8.64E7)) + " Days";
        }
        if (type == TimeUnit.HOURS) {
            return String.valueOf(UtilMath.trim(trim, time / 3600000.0)) + " Hours";
        }
        if (type == TimeUnit.MINUTES) {
            return String.valueOf(UtilMath.trim(trim, time / 60000.0)) + " Minutes";
        }
        if (type == TimeUnit.SECONDS) {
            return String.valueOf(UtilMath.trim(trim, time / 1000.0)) + " Seconds";
        }
        return String.valueOf(UtilMath.trim(trim, (double)time)) + " Milliseconds";
    }
    
    public static boolean elapsed(final long from, final long required) {
        return System.currentTimeMillis() - from > required;
    }
    
    public static long elapsed(final long starttime) {
        return System.currentTimeMillis() - starttime;
    }
    
    public static long left(final long start, final long required) {
        return required + start - System.currentTimeMillis();
    }
    
    public enum TimeUnit
    {
        FIT("FIT", 0), 
        DAYS("DAYS", 1), 
        HOURS("HOURS", 2), 
        MINUTES("MINUTES", 3), 
        SECONDS("SECONDS", 4), 
        MILLISECONDS("MILLISECONDS", 5);
        
        private TimeUnit(final String s, final int n) {
        }
    }
}
