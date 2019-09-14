package net.aluix.pubg.game.loot;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.inventory.ItemStack;

public class WeightedLootTable
{
    public static final Random random;
    private final ItemStack[] lootTable;
    
    static {
        random = new Random();
    }
    
    protected WeightedLootTable(final ItemStack[] lootTable) {
        this.lootTable = lootTable;
    }
    
    public ItemStack loot() {
        final ItemStack stack = this.lootTable[WeightedLootTable.random.nextInt(this.lootTable.length)].clone();
        if (stack.getAmount() > 1) {
            stack.setAmount(WeightedLootTable.random.nextInt(stack.getAmount()) + 1);
        }
        return stack;
    }
    
    public static class WeightedLootTableBuilder
    {
        private ArrayList<ItemStack> stacks;
        private int maxWeight;
        
        public WeightedLootTableBuilder() {
            this.stacks = new ArrayList<ItemStack>();
            this.maxWeight = 10;
        }
        
        public WeightedLootTableBuilder(final int maxWeight) {
            this.stacks = new ArrayList<ItemStack>();
            this.maxWeight = 10;
            this.maxWeight = maxWeight;
        }
        
        public void add(final ItemStack stack, int weight) {
            if (weight < 1) {
                weight = 1;
            }
            if (weight > this.maxWeight) {
                weight = this.maxWeight;
            }
            for (int i = 0; i < weight; ++i) {
                this.stacks.add(stack);
            }
        }
        
        public WeightedLootTable build() {
            WeightedLootTable wlt = null;
            if (this.stacks.size() > 0) {
                final ItemStack[] table = new ItemStack[this.stacks.size()];
                this.stacks.toArray(table);
                wlt = new WeightedLootTable(table);
            }
            return wlt;
        }
    }
}
