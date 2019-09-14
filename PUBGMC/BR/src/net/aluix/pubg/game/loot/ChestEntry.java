package net.aluix.pubg.game.loot;

import org.bukkit.Location;

public class ChestEntry
{
    private final Location location;
    private final int tier;
    
    public ChestEntry(final Location location, final int tier) {
        this.location = location;
        this.tier = tier;
    }
    
    public int getTier() {
        return this.tier;
    }
    
    public Location getLocation() {
        return this.location.clone();
    }
}
