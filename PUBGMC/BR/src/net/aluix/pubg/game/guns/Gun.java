package net.aluix.pubg.game.guns;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.aluix.pubg.game.guns.runnables.Reloader;

public abstract class Gun
{
    private String name;
    private GunType gunType;
    private Material material;
    private int cost;
    private int ammunition;
    private int maxAmmunition;
    private double damage;
    private double reloadTime;
    private boolean autoFire;
    private boolean shotGun;
    
    public Gun(final String name, final GunType gunType, final Material material, final int cost, final int ammunition, final double damage, final double reloadTime, final boolean autoFire, final boolean shotGun) {
        this.name = name;
        this.gunType = gunType;
        this.material = material;
        this.cost = cost;
        this.ammunition = ammunition;
        this.maxAmmunition = ammunition;
        this.damage = damage;
        this.reloadTime = reloadTime;
        this.autoFire = autoFire;
        this.shotGun = shotGun;
    }
    
    public boolean isShotGun() {
        return this.shotGun;
    }
    
    public void setShotGun(final boolean shotGun) {
        this.shotGun = shotGun;
    }
    
    public abstract ItemStack getGunItem();
    
    public void reload(final Player player) {
        new Reloader(player, this, this.reloadTime);
    }
    
    public String getName() {
        return this.name;
    }
    
    public GunType getGunType() {
        return this.gunType;
    }
    
    public int getCost() {
        return this.cost;
    }
    
    public int getAmmunition() {
        return this.ammunition;
    }
    
    public int getMaxAmmunition() {
        return this.maxAmmunition;
    }
    
    public void setAmmunition(final int ammunition) {
        this.ammunition = ammunition;
    }
    
    public double getDamage() {
        return this.damage;
    }
    
    public double getReloadTime() {
        return this.reloadTime;
    }
    
    public boolean hasAutoFire() {
        return this.autoFire;
    }
    
    public Material getMaterial() {
        return this.material;
    }
}
