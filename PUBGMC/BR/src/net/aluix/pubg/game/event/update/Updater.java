package net.aluix.pubg.game.event.update;

import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Updater implements Runnable
{
    private JavaPlugin _plugin;
    
    public Updater(final JavaPlugin plugin) {
        this._plugin = plugin;
        this._plugin.getServer().getScheduler().scheduleSyncRepeatingTask((Plugin)this._plugin, (Runnable)this, 0L, 1L);
    }
    
    @Override
    public void run() {
        UpdateType[] values;
        for (int length = (values = UpdateType.values()).length, i = 0; i < length; ++i) {
            final UpdateType updateType = values[i];
            if (updateType.Elapsed()) {
                this._plugin.getServer().getPluginManager().callEvent((Event)new UpdateEvent(updateType));
            }
        }
    }
}
