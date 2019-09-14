package net.aluix.pubg.game;

import org.bukkit.event.Event;
import org.bukkit.event.server.ServerListPingEvent;

import net.aluix.pubg.Main;

public class GameStateManager
{
    private GameState currentState;
    private MetadataHandler metadataHandler;
    private boolean enabled;
    private Main pluginInstance;
    
    public GameStateManager(final Main pluginInstance) {
        this.currentState = GameState.PREPARING;
        this.metadataHandler = new EmptyMetadataHandler();
        this.enabled = false;
        this.pluginInstance = pluginInstance;
    }
    
    public void onServerListPing(final ServerListPingEvent e) {
        if (this.enabled) {
            if (this.metadataHandler != null) {
                e.setMotd(String.valueOf(this.currentState.toString()) + ";" + this.metadataHandler.getMetadata());
            }
            else {
                e.setMotd(this.currentState.toString());
            }
        }
    }
    
    public void setMetadataHandler(final MetadataHandler handler) {
        this.metadataHandler = handler;
    }
    
    public MetadataHandler getMetadataHandler() {
        return this.metadataHandler;
    }
    
    public void setState(final GameState state) {
        final GameStateChangeEvent event = new GameStateChangeEvent(this.currentState, state);
        this.currentState = state;
        this.pluginInstance.getServer().getPluginManager().callEvent((Event)event);
    }
    
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }
    
    public GameState getState() {
        return this.currentState;
    }
    
    public static class EmptyMetadataHandler implements MetadataHandler
    {
        @Override
        public String getMetadata() {
            return "";
        }
    }
    
    public interface MetadataHandler
    {
        String getMetadata();
    }
}
