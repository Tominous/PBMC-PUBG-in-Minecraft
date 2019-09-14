package net.aluix.pubg.game.guns.guns;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.aluix.pubg.game.guns.Gun;
import net.aluix.pubg.game.guns.GunType;

public class SKS extends Gun
{
    public SKS() {
        super("§cSKS", GunType.SKS, Material.GOLD_AXE, 0, 10, 10.0, 4.0, false, false);
    }
    
    public static ItemStack getItem() {
        final ItemStack ak47 = new ItemStack(Material.GOLD_AXE);
        final ItemMeta ak47Meta = ak47.getItemMeta();
        ak47Meta.setDisplayName("§cSKS");
        ak47.setItemMeta(ak47Meta);
        return ak47;
    }
    
    @Override
    public ItemStack getGunItem() {
        return getItem();
    }
}
