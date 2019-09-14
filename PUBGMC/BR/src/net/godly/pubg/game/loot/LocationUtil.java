package net.godly.pubg.game.loot;

import org.bukkit.Location;
import org.bukkit.World;

public class LocationUtil
{
    public static Location locFromString(final String s, final World world) {
        final String[] split = s.split("\\,");
        final int[] values = new int[split.length];
        for (int i = 0; i < split.length; ++i) {
            values[i] = Integer.valueOf(split[i]);
        }
        if (values.length >= 3) {
            final Location location = new Location(world, (double)values[0], (double)values[1], (double)values[2]);
            if (values.length >= 4) {
                location.setYaw(values[3] * 90.0f);
            }
            return location;
        }
        return null;
    }
    
    public static String buildLocationString(final Location loc) {
        final StringBuilder builder = new StringBuilder();
        builder.append(loc.getBlockX());
        builder.append(",");
        builder.append(loc.getBlockY());
        builder.append(",");
        builder.append(loc.getBlockZ());
        return builder.toString();
    }
    
    public static Location buildLocationFromString(final String s, final World world) {
        return locFromString(s, world);
    }
    
    public static CompactLocation compactFromLocation(final Location location) {
        return new CompactLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }
    
    public static Location compactToLocation(final CompactLocation location, final World world) {
        return location.toLocation(world);
    }
    
    public static class CompactLocation
    {
        private double x;
        private double y;
        private double z;
        private float yaw;
        private float pitch;
        
        public CompactLocation(final double x, final double y, final double z, final float yaw, final float pitch) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.yaw = yaw;
            this.pitch = pitch;
        }
        
        public double getX() {
            return this.x;
        }
        
        public double getY() {
            return this.y;
        }
        
        public double getZ() {
            return this.z;
        }
        
        public float getYaw() {
            return this.yaw;
        }
        
        public float getPitch() {
            return this.pitch;
        }
        
        protected Location toLocation(final World world) {
            final Location loc = new Location(world, this.x, this.y, this.z);
            loc.setYaw(this.yaw);
            loc.setPitch(this.pitch);
            return loc;
        }
    }
}
