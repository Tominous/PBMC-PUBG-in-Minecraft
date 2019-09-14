package net.godly.pubg.player;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.godly.pubg.Main;
import net.godly.pubg.game.guns.Gun;
import net.godly.pubg.game.guns.guns.AK47;
import net.godly.pubg.game.guns.guns.S1897;
import net.godly.pubg.game.guns.guns.SKS;
import net.godly.pubg.game.guns.guns.Scar;

public class PUPlayer
{
    private Collection<Gun> guns;
    private Player player;
    
    public PUPlayer(final Main plugin, final Player player) {
        this.guns = new ArrayList<Gun>();
        this.player = player;
        this.update();
        this.guns.add(new AK47());
        this.guns.add(new SKS());
        this.guns.add(new Scar());
        this.guns.add(new S1897());
    }
    
    public void update() {
        this.player.updateInventory();
        ItemStack[] contents;
        for (int length = (contents = this.player.getInventory().getContents()).length, i = 0; i < length; ++i) {
            final ItemStack item = contents[i];
            if (item != null) {
                item.setDurability((short)(item.getDurability() - 20));
            }
        }
        this.player.removePotionEffect(PotionEffectType.SPEED);
        if (this.player.getItemInHand().getType().equals((Object)Material.IRON_AXE)) {
            this.player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 99999, 0));
        }
    }
    
    public Collection<Gun> getGuns() {
        return this.guns;
    }
    
    public boolean hasGuns() {
        return !this.guns.isEmpty();
    }
    
    public Player getPlayer() {
        return this.player;
    }
}
