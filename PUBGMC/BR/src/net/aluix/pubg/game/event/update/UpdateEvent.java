package net.aluix.pubg.game.event.update;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class UpdateEvent extends Event
{
    private static final HandlerList handlers;
    private UpdateType _type;
    
    static {
        handlers = new HandlerList();
    }
    
    public UpdateEvent(final UpdateType example) {
        this._type = example;
    }
    
    public UpdateType getType() {
        return this._type;
    }
    
    public HandlerList getHandlers() {
        return UpdateEvent.handlers;
    }
    
    public static HandlerList getHandlerList() {
        return UpdateEvent.handlers;
    }
}
