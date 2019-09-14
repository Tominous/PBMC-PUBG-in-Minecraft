package net.aluix.pubg.game.parachute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import net.aluix.pubg.Main;

public class ParachuteListener implements Listener
{
    public ItemStack parachute;
    public ItemStack autoParachute;
    public boolean dobblespace;
    public boolean singleuse;
    public boolean damageParachute;
    private boolean useLeash;
    private boolean useRecipe;
    public boolean useAutoParachute;
    public boolean error;
    public int time;
    public int failchance;
    public int fallBlocks;
    public double fallspeed;
    public double speed;
    public Sound activatesound;
    public Sound disablesound;
    public Sound failsound;
    public static List<String> playerparachute;
    public HashMap<String, Chicken> playerChicken;
    public HashMap<String, Block[]> playerBlocks;
    
    public ParachuteListener() {
        this.singleuse = false;
        this.time = -1;
        this.fallspeed = 0.4;
        this.speed = 0.8;
        ParachuteListener.playerparachute = new ArrayList<String>();
        this.playerChicken = new HashMap<String, Chicken>();
        this.playerBlocks = new HashMap<String, Block[]>();
    }
    
    @EventHandler
    public void onParaInteract(final PlayerInteractEvent e) {
        if ((e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) && e.getMaterial().equals((Object)Material.BANNER)) {
            e.setCancelled(true);
            this.activateParachute(e.getPlayer(), e.getPlayer().getItemInHand(), true);
        }
    }
    
    public void activateParachute(final Player p, final ItemStack stack, final boolean normalParachute) {
        if (!ParachuteListener.playerparachute.contains(p.getName())) {
            if (this.singleuse) {
                if (normalParachute) {
                    final int first = p.getInventory().first(this.parachute);
                    if (stack.getAmount() == 1) {
                        p.getInventory().remove(this.parachute);
                    }
                    else {
                        stack.setAmount(stack.getAmount() - 1);
                        p.getInventory().setItem(first, stack);
                    }
                }
                else {
                    final int first = p.getInventory().first(stack);
                    if (stack.getAmount() == 1) {
                        p.getInventory().remove(stack);
                    }
                    else {
                        stack.setAmount(stack.getAmount() - 1);
                        p.getInventory().setItem(first, stack);
                    }
                }
            }
            final Random rnd = new Random();
            final int zufall = rnd.nextInt(99) + 1;
            if (zufall <= this.failchance) {
                p.playSound(p.getLocation(), this.failsound, 2.0f, 2.0f);
                return;
            }
            p.sendMessage(String.valueOf(Main.CHAT_PREFIX) + "You can remove your parachute by sneaking. Good luck!");
            p.playSound(p.getLocation(), this.activatesound, 2.0f, 2.0f);
            ParachuteListener.playerparachute.add(p.getName());
            final Chicken chick = (Chicken)p.getWorld().spawnCreature(p.getLocation(), CreatureType.CHICKEN);
            p.setPassenger((Entity)chick);
            if (this.useLeash) {
                chick.setLeashHolder((Entity)p);
            }
            this.playerChicken.put(p.getName(), chick);
            if (this.time >= 0) {
                Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)Main.getInstance(), (Runnable)new Runnable() {
                    @Override
                    public void run() {
                        ParachuteListener.playerparachute.remove(p.getName());
                    }
                }, (long)(this.time * 20));
            }
        }
    }
    
    public void removeParachute(final Player p) {
        ParachuteListener.playerparachute.remove(p.getName());
        p.playSound(p.getLocation(), this.disablesound, 2.0f, 2.0f);
        this.playerChicken.get(p.getName()).remove();
    }
    
    @EventHandler
    public void onPlayerMove(final PlayerMoveEvent e) {
        final Player p = e.getPlayer();
        if (p.getLocation().subtract(0.0, 1.0, 0.0).getBlock().isEmpty() && ParachuteListener.playerparachute.contains(p.getName())) {
            p.setVelocity(new Vector(p.getLocation().getDirection().getX() * this.speed, p.getVelocity().getY() * this.fallspeed, p.getLocation().getDirection().getZ() * this.speed));
            p.setFallDistance(0.0f);
        }
    }
    
    @EventHandler
    public void onSneak(final PlayerToggleSneakEvent e) {
        final Player p = e.getPlayer();
        if (ParachuteListener.playerparachute.contains(p.getName())) {
            this.removeParachute(p);
        }
    }
}
