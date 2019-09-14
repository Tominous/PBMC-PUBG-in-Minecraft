package net.godly.pubg.game.guns.runnables;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import net.godly.pubg.Main;
import net.godly.pubg.game.guns.Gun;
import net.godly.pubg.utils.PacketUtils;

public class Reloader extends BukkitRunnable
{
    Player player;
    Gun gun;
    double duration;
    int ticksTilExperience;
    
    public Reloader(final Player player, final Gun gun, final double duration) {
        this.player = player;
        this.gun = gun;
        this.duration = duration;
        this.ticksTilExperience = (int)Math.round(duration * 20.0 / 7.0);
        this.runTaskTimer((Plugin)Main.getInstance(), (long)this.ticksTilExperience, (long)this.ticksTilExperience);
        new BukkitRunnable() {
            int count = 0;
            
            public void run() {
                switch (++this.count) {
                    case 1: {
                        PacketUtils.sendActionBar(player, ChatColor.GREEN + "Reloading.", 1);
                        break;
                    }
                    case 2: {
                        PacketUtils.sendActionBar(player, ChatColor.GREEN + "Reloading..", 1);
                        break;
                    }
                    case 3: {
                        PacketUtils.sendActionBar(player, ChatColor.GREEN + "Reloading...", 1);
                        break;
                    }
                    case 4: {
                        PacketUtils.sendActionBar(player, ChatColor.GREEN + "Reloading....", 1);
                        break;
                    }
                    case 5: {
                        PacketUtils.sendActionBar(player, ChatColor.GREEN + "Reloading.....", 1);
                        this.count = 0;
                        break;
                    }
                }
                if (player.getLevel() >= 1) {
                    PacketUtils.sendActionBar(player, ChatColor.GREEN + "Reloaded!", 1);
                    this.cancel();
                }
            }
        }.runTaskTimer((Plugin)Main.getInstance(), 0L, 5L);
    }
    
    public void run() {
        this.player.giveExp(1);
        if (this.player.getLevel() >= 1) {
            this.gun.setAmmunition(this.gun.getMaxAmmunition());
            this.player.setLevel(this.gun.getMaxAmmunition());
            this.player.setExp(0.0f);
            this.cancel();
        }
    }
}
