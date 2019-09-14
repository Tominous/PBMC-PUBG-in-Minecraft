package net.godly.pubg.game.event;

import java.util.UUID;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LobbyStatsUpdateEvent extends Event
{
    private static final HandlerList handlers;
    private UUID player;
    private LobbyStatsType type;
    private int before;
    private int now;
    
    static {
        handlers = new HandlerList();
    }
    
    public LobbyStatsUpdateEvent(final UUID player, final LobbyStatsType type, final int before, final int now) {
        this.player = player;
        this.type = type;
        this.before = before;
        this.now = now;
    }
    
    public HandlerList getHandlers() {
        return LobbyStatsUpdateEvent.handlers;
    }
    
    public static HandlerList getHandlerList() {
        return LobbyStatsUpdateEvent.handlers;
    }
    
    public UUID getPlayer() {
        return this.player;
    }
    
    public LobbyStatsType getType() {
        return this.type;
    }
    
    public int getBefore() {
        return this.before;
    }
    
    public int getNow() {
        return this.now;
    }
}
