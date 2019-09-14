package net.aluix.pubg.game.guns.guns;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.aluix.pubg.game.guns.Gun;
import net.aluix.pubg.game.guns.GunType;

public class Scar extends Gun
{
    public Scar() {
        super("§eSCAR-L", GunType.FAMAS, Material.DIAMOND_AXE, 0, 30, 6.0, 2.1, true, false);
    }
    
    public static ItemStack getItem() {
        final ItemStack ak47 = new ItemStack(Material.DIAMOND_AXE);
        final ItemMeta ak47Meta = ak47.getItemMeta();
        ak47Meta.setDisplayName("§eSCAR-L");
        ak47.setItemMeta(ak47Meta);
        return ak47;
    }
    
    @Override
    public ItemStack getGunItem() {
        return getItem();
    }
}
