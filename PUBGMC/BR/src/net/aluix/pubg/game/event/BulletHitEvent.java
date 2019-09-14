package net.aluix.pubg.game.event;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.aluix.pubg.game.guns.Gun;

public class BulletHitEvent extends Event
{
    private static final HandlerList handlers;
    private Player shooter;
    private Player victim;
    private Projectile bullet;
    private Gun gun;
    
    static {
        handlers = new HandlerList();
    }
    
    public BulletHitEvent(final Player shooter, final Player victim, final Projectile bullet, final Gun gun) {
        this.shooter = shooter;
        this.victim = victim;
        this.bullet = bullet;
        this.gun = gun;
    }
    
    public Player getShooter() {
        return this.shooter;
    }
    
    public Player getVictim() {
        return this.victim;
    }
    
    public Projectile getBullet() {
        return this.bullet;
    }
    
    public Gun getGun() {
        return this.gun;
    }
    
    public HandlerList getHandlers() {
        return BulletHitEvent.handlers;
    }
    
    public static HandlerList getHandlerList() {
        return BulletHitEvent.handlers;
    }
}
