package net.aluix.pubg.game;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.aluix.pubg.Main;

public class StartTimerFullSecondAndRunningEvent extends Event
{
    private static final HandlerList handlers;
    private Main gameMode;
    private int seconds;
    
    static {
        handlers = new HandlerList();
    }
    
    public StartTimerFullSecondAndRunningEvent(final Main gameMode, final int seconds) {
        this.gameMode = gameMode;
        this.seconds = seconds;
    }
    
    public int getSecondsLeft() {
        return this.seconds;
    }
    
    public Main getGameMode() {
        return this.gameMode;
    }
    
    public HandlerList getHandlers() {
        return StartTimerFullSecondAndRunningEvent.handlers;
    }
    
    public static HandlerList getHandlerList() {
        return StartTimerFullSecondAndRunningEvent.handlers;
    }
}
