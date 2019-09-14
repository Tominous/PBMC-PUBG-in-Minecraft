package net.aluix.pubg.game.guns.guns;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.aluix.pubg.game.guns.Gun;
import net.aluix.pubg.game.guns.GunType;

public class S1897 extends Gun
{
    public S1897() {
        super("§cS1897", GunType.S1897, Material.IRON_PICKAXE, 0, 5, 22.0, 6.0, false, true);
    }
    
    public static ItemStack getItem() {
        final ItemStack ak47 = new ItemStack(Material.IRON_PICKAXE);
        final ItemMeta ak47Meta = ak47.getItemMeta();
        ak47Meta.setDisplayName("§cS1897");
        ak47.setItemMeta(ak47Meta);
        return ak47;
    }
    
    @Override
    public ItemStack getGunItem() {
        return getItem();
    }
}
