package net.godly.pubg.game.guns.guns;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.godly.pubg.game.guns.Gun;
import net.godly.pubg.game.guns.GunType;

public class AK47 extends Gun
{
    public AK47() {
        super("§cAKM", GunType.AK47, Material.GOLD_SPADE, 0, 30, 7.0, 2.5, true, false);
    }
    
    public static ItemStack getItem() {
        final ItemStack ak47 = new ItemStack(Material.GOLD_SPADE);
        final ItemMeta ak47Meta = ak47.getItemMeta();
        ak47Meta.setDisplayName("§cAKM");
        ak47.setItemMeta(ak47Meta);
        return ak47;
    }
    
    @Override
    public ItemStack getGunItem() {
        return getItem();
    }
}
