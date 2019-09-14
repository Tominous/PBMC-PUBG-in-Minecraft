package net.aluix.pubg.game.guns.runnables;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.Event;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import net.aluix.pubg.game.event.WeaponFireEvent;
import net.aluix.pubg.game.guns.Gun;
import net.aluix.pubg.utils.PlayerUtils;

public class Gunfire extends BukkitRunnable
{
    HashMap<UUID, Long> firing;
    Player player;
    Gun gun;
    
    public Gunfire(final HashMap<UUID, Long> firing, final Player player, final Gun gun) {
        this.firing = firing;
        this.player = player;
        this.gun = gun;
    }
    
    public void run() {
        if (this.gun.getAmmunition() <= 0) {
            this.cancel();
            return;
        }
        final Vector playerDir = this.player.getLocation().getDirection();
        final Vector dirVel = playerDir.multiply(5);
        final Snowball bullet = (Snowball)this.player.getWorld().spawnEntity(PlayerUtils.getRightHeadLocation((LivingEntity)this.player), EntityType.SNOWBALL);
        bullet.setVelocity(dirVel);
        bullet.setShooter((ProjectileSource)this.player);
        bullet.setBounce(false);
        this.player.getWorld().playSound(this.player.getLocation(), Sound.ENDERDRAGON_HIT, 1.0f, 1.0f);
        this.gun.setAmmunition(this.gun.getAmmunition() - 1);
        final WeaponFireEvent calledEvent = new WeaponFireEvent(this.player, this.gun, (Projectile)bullet);
        Bukkit.getPluginManager().callEvent((Event)calledEvent);
        if (!this.firing.containsKey(this.player.getUniqueId())) {
            this.cancel();
        }
    }
}
