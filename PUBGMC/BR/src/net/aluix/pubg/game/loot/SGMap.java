package net.aluix.pubg.game.loot;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

public class SGMap
{
    private static final Random random;
    private final World world;
    private final ArrayList<Location> spawns;
    private final ArrayList<ChestEntry> chests;
    private final Location mid;
    private final String author;
    
    static {
        random = new Random();
    }
    
    private SGMap(final World world, final ArrayList<Location> spawns, final ArrayList<ChestEntry> chests, final Location mid, final String author) {
        this.world = world;
        this.spawns = spawns;
        this.chests = chests;
        this.mid = mid;
        this.author = author;
    }
    
    public ArrayList<ChestEntry> getChestLocations() {
        return (ArrayList<ChestEntry>)this.chests.clone();
    }
    
    public ArrayList<Location> getSpawns() {
        return (ArrayList<Location>)this.spawns.clone();
    }
    
    public Location getRandomSpawnAndRemove() {
        if (this.spawns.size() > 0) {
            final Location loc = this.spawns.get(SGMap.random.nextInt(this.spawns.size()));
            this.spawns.remove(loc);
            loc.add(0.5, 0.0, 0.5);
            return loc;
        }
        return this.mid.clone();
    }
    
    public World getWorld() {
        return this.world;
    }
    
    public Location getMid() {
        return this.mid.clone();
    }
    
    public String getAuthor() {
        return this.author;
    }
    
    public static SGMap loadMap(final String name) {
        final File file = new File(name);
        if (file.exists()) {
            final File config = new File(file, "mapconfig.yml");
            if (config.exists()) {
                final WorldCreator wc = new WorldCreator(name);
                final World world = wc.createWorld();
                world.setAutoSave(false);
                world.setPVP(true);
                world.setDifficulty(Difficulty.NORMAL);
                world.setGameRuleValue("doDaylightCycle", "false");
                world.setGameRuleValue("mobGriefing", "false");
                world.setGameRuleValue("doMobSpawning", "false");
                world.setGameRuleValue("doFireTick", "false");
                world.setGameRuleValue("keepInventory", "false");
                world.setGameRuleValue("commandBlockOutput", "false");
                world.setSpawnFlags(false, false);
                for (final Entity entity : world.getEntities()) {
                    if (!(entity instanceof Player) && !(entity instanceof Villager)) {
                        entity.remove();
                    }
                }
                final YamlConfiguration configuration = YamlConfiguration.loadConfiguration(config);
                final List<String> spawnStrings = (List<String>)configuration.getStringList("spawns");
                final List<String> chestStrings = (List<String>)configuration.getStringList("chests");
                final ArrayList<Location> spawns = new ArrayList<Location>();
                final ArrayList<ChestEntry> chests = new ArrayList<ChestEntry>();
                for (final String s : spawnStrings) {
                    spawns.add(LocationUtil.buildLocationFromString(s, world));
                }
                for (final String s : chestStrings) {
                    final String[] sA = s.split("\\.");
                    chests.add(new ChestEntry(LocationUtil.buildLocationFromString(sA[0], world), Integer.valueOf(sA[1])));
                }
                String s = configuration.getString("mid");
                final String auth = configuration.getString("author", "");
                final SGMap mapSession = new SGMap(world, spawns, chests, LocationUtil.buildLocationFromString(s, world), auth);
                return mapSession;
            }
        }
        return null;
    }
}
