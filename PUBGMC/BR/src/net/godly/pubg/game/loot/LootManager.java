package net.godly.pubg.game.loot;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.inventory.Inventory;

public class LootManager
{
    private Random random;
    private LootWrapper tier_0;
    private LootWrapper tier_1;
    
    public LootManager() {
        this.random = new Random();
    }
    
    public void load() {
        this.tier_0 = new LootWrapper("loottable_standard.yml").load();
        this.tier_1 = new LootWrapper("loottable_high.yml").load();
    }
    
    public void fillChest(final Inventory inventory, final int tier) {
        switch (tier) {
            case 1: {
                this.fillChest(inventory, this.tier_0);
                break;
            }
            case 2: {
                this.fillChest(inventory, this.tier_1);
                break;
            }
        }
    }
    
    private void fillChest(final Inventory inventory, final LootWrapper wrapper) {
        final int slotDiff = wrapper.getMaxSlots() - wrapper.getMinSlots() + 1;
        final int slotCount = wrapper.getMinSlots() + this.random.nextInt(slotDiff);
        final ArrayList<Integer> exclusions = new ArrayList<Integer>();
        for (int i = 0; i < slotCount; ++i) {
            final int slot = this.generateRandom(0, inventory.getSize() - 1, exclusions);
            exclusions.add(slot);
            inventory.setItem(slot, wrapper.getTable().loot());
        }
    }
    
    private int generateRandom(final int start, final int end, final ArrayList<Integer> excludeRows) {
        final int range = end - start + 1;
        int randNum = this.random.nextInt(range) + 1;
        while (excludeRows.contains(this.random)) {
            randNum = this.random.nextInt(range) + 1;
        }
        return randNum - 1;
    }
}
