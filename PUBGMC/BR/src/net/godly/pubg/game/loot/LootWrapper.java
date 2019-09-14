package net.godly.pubg.game.loot;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public class LootWrapper
{
    private int minSlots;
    private int maxSlots;
    private WeightedLootTable table;
    private String name;
    
    public LootWrapper(final String name) {
        this.name = name;
    }
    
    protected LootWrapper load() {
        this.table = this.loadTable(this.name);
        return this;
    }
    
    private WeightedLootTable loadTable(final String name) {
        System.out.println("Loading loot table " + name + "...");
        final WeightedLootTable.WeightedLootTableBuilder builder = new WeightedLootTable.WeightedLootTableBuilder(100);
        try {
            Throwable t = null;
            try {
                final BufferedReader br = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(name)));
                try {
                    final YamlConfiguration lootConfig = YamlConfiguration.loadConfiguration((Reader)br);
                    this.minSlots = lootConfig.getInt("minSlots", 1);
                    this.maxSlots = lootConfig.getInt("maxSlots", 7);
                    final ConfigurationSection itemSection = lootConfig.getConfigurationSection("items");
                    for (final String s : itemSection.getKeys(false)) {
                        final Material m = Material.getMaterial(s);
                        final ConfigurationSection stackSection = itemSection.getConfigurationSection(s);
                        final int amount = stackSection.getInt("amount", 1);
                        final int probability = stackSection.getInt("probability", 1);
                        final short damage = (short)stackSection.getInt("damage", 0);
                        final ItemStack stack = new ItemStack(m, amount, damage);
                        builder.add(stack, probability);
                    }
                }
                finally {
                    if (br != null) {
                        br.close();
                    }
                }
            }
            finally {
                if (t == null) {
                    final Throwable t2 = null;
                    t = t2;
                }
                else {
                    final Throwable t2 = null;
                    if (t != t2) {
                        t.addSuppressed(t2);
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return builder.build();
    }
    
    public int getMinSlots() {
        return this.minSlots;
    }
    
    public int getMaxSlots() {
        return this.maxSlots;
    }
    
    public WeightedLootTable getTable() {
        return this.table;
    }
}
