package net.aluix.pubg.game.event;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import net.aluix.pubg.game.guns.Gun;

public class WeaponFireEvent extends PlayerEvent
{
    private static final HandlerList handlers;
    Gun gun;
    Projectile bullet;
    
    static {
        handlers = new HandlerList();
    }
    
    public WeaponFireEvent(final Player shooter, final Gun gun, final Projectile bullet) {
        super(shooter);
        this.gun = gun;
        this.bullet = bullet;
    }
    
    public Projectile getBullet() {
        return this.bullet;
    }
    
    public Gun getGun() {
        return this.gun;
    }
    
    public HandlerList getHandlers() {
        return WeaponFireEvent.handlers;
    }
    
    public static HandlerList getHandlerList() {
        return WeaponFireEvent.handlers;
    }
}
