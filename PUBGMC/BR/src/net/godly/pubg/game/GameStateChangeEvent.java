package net.godly.pubg.game;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameStateChangeEvent extends Event
{
    private static final HandlerList handlers;
    private GameState from;
    private GameState to;
    
    static {
        handlers = new HandlerList();
    }
    
    public GameStateChangeEvent(final GameState from, final GameState to) {
        this.from = from;
        this.to = to;
    }
    
    public GameState getFrom() {
        return this.from;
    }
    
    public GameState getTo() {
        return this.to;
    }
    
    public HandlerList getHandlers() {
        return GameStateChangeEvent.handlers;
    }
    
    public static HandlerList getHandlerList() {
        return GameStateChangeEvent.handlers;
    }
}
