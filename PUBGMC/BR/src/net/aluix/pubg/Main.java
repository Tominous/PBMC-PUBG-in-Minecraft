package net.aluix.pubg;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import net.aluix.pubg.game.GameListener;
import net.aluix.pubg.game.GameState;
import net.aluix.pubg.game.GameStateManager;
import net.aluix.pubg.game.achievment.Achievemnt;
import net.aluix.pubg.game.commands.MainCommand;
import net.aluix.pubg.game.event.LobbyStatsUpdateListener;
import net.aluix.pubg.game.event.update.Updater;
import net.aluix.pubg.game.guns.BulletHitListener;
import net.aluix.pubg.game.guns.Gun;
import net.aluix.pubg.game.guns.WeaponFireListener;
import net.aluix.pubg.game.guns.guns.S1897;
import net.aluix.pubg.game.guns.guns.SKS;
import net.aluix.pubg.game.guns.guns.Scar;
import net.aluix.pubg.game.loot.LootWrapper;
import net.aluix.pubg.game.loot.SGMap;
import net.aluix.pubg.game.loot.WeightedLootTable;
import net.aluix.pubg.game.parachute.ParachuteListener;
import net.aluix.pubg.game.timer.StartTimer;
import net.aluix.pubg.player.PUPlayer;

public class Main extends JavaPlugin
{
    Collection<PUPlayer> csPlayers;
    public static String CHAT_PREFIX;
    public int minplayers;
    public static Main instance;
    public static final Collection<String> gunNames;
    public GameStateManager gameStateManager;
    boolean triggerStartTimerEvents;
    public StartTimer ts;
    public static final Random gRandom;
    public static String ServerName;
    public static String ScoreBoardTitle;
    private WeightedLootTable lootLesser;
    public static WeightedLootTable lootGreater;
    protected SGMap mapSession;
    public HashMap<String, Integer> killscounter;
    public String worldName;
    public double SpawnX;
    public double SpawnZ;
    public double SpawnY;
    private FileConfiguration PlayersConfig;
    private File customPlayersConfig;
    public FileConfiguration cfg;
    public File f;
    public int lootBlocks;
    Random random;
    public static String mysqlprefix;
    
    static {
        gRandom = new Random();
        gunNames = new ArrayList<String>() {
            {
                this.add("§cAKM");
                this.add("§eSCAR-L");
                this.add("Glock");
                this.add("M416");
                this.add("FAMAS");
                this.add("USP");
                this.add("§cSKS");
                this.add("§cS1897");
            }
        };
        Main.mysqlprefix = "PUBG";
    }
    
    public FileConfiguration getCfg() {
        return this.cfg;
    }
    
    public void setCfg(final FileConfiguration cfg) {
        this.cfg = cfg;
    }
    
    public Main() {
        this.random = new Random();
        this.csPlayers = new ArrayList<PUPlayer>();
    }
    
    public void onEnable() {
        (Main.instance = this).setupPlayersConfig();
        this.killscounter = new HashMap<String, Integer>();
        this.triggerStartTimerEvents = true;
        this.getServer().getPluginManager().registerEvents((Listener)new GameListener(), (Plugin)this);
        (this.gameStateManager = new GameStateManager(this)).setEnabled(true);
        this.mapSession = SGMap.loadMap("battlegrounds");
        this.getServer().getPluginManager().registerEvents((Listener)new LobbyStatsUpdateListener(), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new ParachuteListener(), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new BulletHitListener(), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new WeaponFireListener(), (Plugin)this);
        this.getCommand("pubg").setExecutor((CommandExecutor)new MainCommand());
        this.f = new File("plugins/PBMC/config.yml");
        this.cfg = (FileConfiguration)YamlConfiguration.loadConfiguration(this.f);
        new Updater(getInstance()).run();
		 this.gameStateManager.setState(GameState.WAITING);
        try {
			this.setUpLobby();
		} catch (Exception e) {
			e.printStackTrace();
		}
        this.LoadConfiguration();
        Main.CHAT_PREFIX = this.cfg.getString("ChatPrefix");
        Main.ScoreBoardTitle = this.cfg.getString("ScoreBoard.Title");
        this.minplayers = this.cfg.getInt("MinPlayersToStart");
        this.SpawnX = this.cfg.getDouble("Lobby.SpawnX");
        this.SpawnY = this.cfg.getDouble("Lobby.SpawnY");
        this.SpawnZ = this.cfg.getDouble("Lobby.SpawnZ");
        this.worldName = this.cfg.getString("Match.WorldName");
        Main.ServerName = this.cfg.getString("ScoreBoard.ServerName");
        ts = new StartTimer(this);
        ts.runTaskTimer(this, 0L, 1L);
    }
    
    public static Main getInstance() {
        return Main.instance;
    }
    
    public String getChatPrefix() {
        return Main.CHAT_PREFIX;
    }
    
    private boolean setUpLobby() throws Exception {
        final World worldSpawn = this.getServer().getWorld("battlegrounds");
        if (worldSpawn != null) {
            worldSpawn.setAutoSave(false);
            worldSpawn.setPVP(false);
            worldSpawn.setDifficulty(Difficulty.NORMAL);
            worldSpawn.setGameRuleValue("doDaylightCycle", "false");
            worldSpawn.setGameRuleValue("mobGriefing", "false");
            worldSpawn.setGameRuleValue("doMobSpawning", "false");
            worldSpawn.setGameRuleValue("doFireTick", "false");
            worldSpawn.setGameRuleValue("keepInventory", "true");
            worldSpawn.setSpawnFlags(false, false);
            for (final Entity entity : worldSpawn.getEntities()) {
                if (!(entity instanceof Player)) {
                    entity.remove();
                }
            }
            try {
            	System.out.println("Yee");
                this.preloadChunks();
                System.out.println("Yee");
                this.buildLootTables();
                System.out.println("Yee");
                Bukkit.getWorld("battlegrounds").getWorldBorder().setCenter(new Location(Bukkit.getWorld("battlegrounds"), 0.0, 0.0, 0.0));
                Bukkit.getWorld("battlegrounds").getWorldBorder().setSize(100.0);
                this.clearPlayerData();
                
                fill();
            }
            catch (IOException e) {
                this.getLogger().warning("Could not clear playerdata! Printing stacktrace...");
                e.printStackTrace();
            }
            return true;
        }
        System.out.println(String.valueOf(Main.CHAT_PREFIX) + "Please create a World called 'battlegrounds'");
        return false;
    }
    
    public GameStateManager getGameStateManager() {
        return this.gameStateManager;
    }
    
    private void clearPlayerData() throws IOException {
        final File spawnFolder = new File("battlegrounds");
        if (spawnFolder.exists()) {
            final File playerData = new File(spawnFolder, "playerdata");
            if (playerData.exists()) {
                final File[] playerFiles = playerData.listFiles();
                File[] array;
                for (int length = (array = playerFiles).length, i = 0; i < length; ++i) {
                    final File file = array[i];
                    file.delete();
                }
            }
        }
    }
    
    private void populateMap() {
    }
    
    public void LoadConfiguration() {
        this.cfg.options().copyDefaults(true);
        this.cfg.options().header("################################# PUBGMC Config #################################");
        this.cfg.options().copyHeader(true);
        this.cfg.addDefault("ChatPrefix", (Object)ChatColor.translateAlternateColorCodes('&', "&6>> &4&lBattlegrounds &0 | &7"));
        this.cfg.addDefault("RestartServerAfterMatch", (Object)false);
        this.cfg.addDefault("ScoreBoard.Title", (Object)ChatColor.translateAlternateColorCodes('&', "&4&lBattlegrounds"));
        this.cfg.addDefault("MinPlayersToStart", (Object)4);
        this.cfg.addDefault("Lobby.SpawnX", (Object)0.0);
        this.cfg.addDefault("Lobby.SpawnY", (Object)0.0);
        this.cfg.addDefault("Lobby.SpawnZ", (Object)0.0);
        this.cfg.addDefault("Match.WorldName", (Object)"battlegrounds");
        this.cfg.addDefault("ScoreBoard.ServerName", (Object)ChatColor.translateAlternateColorCodes('&', "&4Unknown Server"));
        try {
            this.cfg.save(this.f);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void fill() {
        final World w = Bukkit.getWorld("battlegrounds");
        for (Chunk c : w.getLoadedChunks()) {
            for (BlockState block: c.getTileEntities()) {
                if (block instanceof InventoryHolder) {
                    final Random rd = new Random();
                    final Random rd2 = new Random();
                    final InventoryHolder chest = (InventoryHolder)block;
                    chest.getInventory().clear();
                    final int items = rd.nextInt(3);
                    if(Main.lootGreater == null) {
                    	System.out.println("Loottable seems to be null, rebuilding loottable");
                    	Main.getInstance().buildLootTables();
                    }
                    if (items == 3) {
                        chest.getInventory().setItem(rd2.nextInt(26), Main.lootGreater.loot());
                        chest.getInventory().setItem(rd2.nextInt(26), Main.lootGreater.loot());
                        chest.getInventory().setItem(rd2.nextInt(26), Main.lootGreater.loot());
                        chest.getInventory().setItem(rd2.nextInt(26), Main.lootGreater.loot());
                    }
                    else if (items == 2) {
                        chest.getInventory().setItem(rd2.nextInt(26), Main.lootGreater.loot());
                        chest.getInventory().setItem(rd2.nextInt(26), Main.lootGreater.loot());
                        chest.getInventory().setItem(rd2.nextInt(26), Main.lootGreater.loot());
                    }
                    else if (items == 1) {
                        chest.getInventory().setItem(rd2.nextInt(26), Main.lootGreater.loot());
                        chest.getInventory().setItem(rd2.nextInt(26), Main.lootGreater.loot());
                    }
                    else if (items == 0) {
                        chest.getInventory().setItem(rd2.nextInt(26), Main.lootGreater.loot());
                    }
                }
            }
        }
    }
    
    public boolean getTriggerStartTimerEvents() {
        return this.triggerStartTimerEvents;
    }
    
    private void buildLootTables() {
        final WeightedLootTable.WeightedLootTableBuilder builderLesser = new WeightedLootTable.WeightedLootTableBuilder(110);
        builderLesser.add(new ItemStack(Material.IRON_INGOT, 8), 110);
        builderLesser.add(new ItemStack(Material.GOLD_INGOT, 8), 90);
        builderLesser.add(new ItemStack(Material.DIAMOND, 5), 50);
        builderLesser.add(new ItemStack(Material.ARROW, 16), 45);
        builderLesser.add(new ItemStack(Material.ENDER_PEARL, 4), 30);
        builderLesser.add(new ItemStack(Material.WATER_BUCKET, 1), 3);
        builderLesser.add(new ItemStack(Material.EXP_BOTTLE, 5), 50);
        builderLesser.add(new ItemStack(Material.LEATHER, 10), 30);
        builderLesser.add(new ItemStack(Material.WEB, 16), 30);
        builderLesser.add(new ItemStack(Material.BOW, 1), 30);
        builderLesser.add(new ItemStack(Material.LOG, 16), 65);
        builderLesser.add(new ItemStack(Material.BREAD, 12), 40);
        builderLesser.add(new ItemStack(Material.LAVA_BUCKET, 1), 3);
        builderLesser.add(new ItemStack(Material.COOKED_BEEF, 8), 40);
        builderLesser.add(new ItemStack(Material.GOLDEN_APPLE, 1), 8);
        builderLesser.add(new ItemStack(Material.FISHING_ROD, 1), 8);
        this.lootLesser = builderLesser.build();
        final WeightedLootTable.WeightedLootTableBuilder builderGreater = new WeightedLootTable.WeightedLootTableBuilder();
        builderGreater.add(new ItemStack(Material.ANVIL, 1, (short)2), 10);
        final ItemStack enchBookSharp = new ItemStack(Material.ENCHANTED_BOOK, 1);
        final ItemMeta enchBookSharpMeta = enchBookSharp.getItemMeta();
        enchBookSharpMeta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
        enchBookSharp.setItemMeta(enchBookSharpMeta);
        builderGreater.add(enchBookSharp, 3);
        final ItemStack enchBookProt = new ItemStack(Material.ENCHANTED_BOOK, 1);
        final ItemMeta enchBookProtMeta = enchBookProt.getItemMeta();
        enchBookProtMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true);
        enchBookProt.setItemMeta(enchBookProtMeta);
        builderGreater.add(enchBookProt, 3);
        final ItemStack enchBookInfinity = new ItemStack(Material.ENCHANTED_BOOK, 1);
        final ItemMeta enchBookInfinityMeta = enchBookInfinity.getItemMeta();
        enchBookInfinityMeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        enchBookInfinity.setItemMeta(enchBookInfinityMeta);
        builderGreater.add(enchBookInfinity, 3);
        final ItemStack enchBookPower = new ItemStack(Material.ENCHANTED_BOOK, 1);
        final ItemMeta enchBookPowerMeta = enchBookPower.getItemMeta();
        enchBookPowerMeta.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
        enchBookPower.setItemMeta(enchBookPowerMeta);
        builderGreater.add(enchBookPower, 3);
        final ItemStack grenade = new ItemStack(Material.TNT);
        final ItemMeta grenademeta = grenade.getItemMeta();
        grenademeta.setDisplayName("§cM67-Grenade");
        grenade.setItemMeta(grenademeta);
        final ItemStack sniper = new ItemStack(Material.GOLD_SPADE);
        final ItemMeta snipermeta = sniper.getItemMeta();
        snipermeta.setDisplayName("§cAKM");
        sniper.setItemMeta(snipermeta);
        builderGreater.add(new ItemStack(Material.IRON_INGOT, 12), 7);
        builderGreater.add(new ItemStack(Material.GOLD_INGOT, 10), 9);
        builderGreater.add(new ItemStack(Material.DIAMOND, 8), 9);
        builderGreater.add(new ItemStack(Material.GOLDEN_APPLE, 1), 7);
        final ItemStack energydrink = new ItemStack(Material.POTION, 1, (short)16386);
        final ItemMeta drinkmeta = energydrink.getItemMeta();
        drinkmeta.setDisplayName("§2Energydrink");
        energydrink.setItemMeta(drinkmeta);
        builderGreater.add(energydrink, 7);
        final ItemStack smallmedikit = new ItemStack(Material.POTION, 1, (short)16389);
        final ItemMeta smallmedikitmeta = smallmedikit.getItemMeta();
        smallmedikitmeta.setDisplayName("§2Throwable MediKit");
        smallmedikit.setItemMeta(smallmedikitmeta);
        builderGreater.add(smallmedikit, 7);
        final ItemStack medikit = new ItemStack(Material.POTION, 1, (short)8289);
        final ItemMeta medimeta = medikit.getItemMeta();
        medimeta.setDisplayName("§2MediKit");
        medikit.setItemMeta(medimeta);
        builderGreater.add(medikit, 7);
        builderGreater.add(S1897.getItem(), 10);
        builderGreater.add(new ItemStack(Material.COOKED_BEEF, 30), 1);
        final ItemStack hemd = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
        final ItemMeta tshirt = hemd.getItemMeta();
        tshirt.setDisplayName("§8T-Shirt");
        hemd.setItemMeta(tshirt);
        final ItemStack helm = new ItemStack(Material.CHAINMAIL_HELMET);
        final ItemMeta schuss = helm.getItemMeta();
        schuss.setDisplayName("§8Cap");
        helm.setItemMeta(schuss);
        final ItemStack hose = new ItemStack(Material.CHAINMAIL_LEGGINGS);
        final ItemMeta leggings = hose.getItemMeta();
        leggings.setDisplayName("§8Jeans");
        hose.setItemMeta(leggings);
        final ItemStack boots = new ItemStack(Material.CHAINMAIL_BOOTS);
        final ItemMeta schuhe = boots.getItemMeta();
        schuhe.setDisplayName("§8Sneaker");
        boots.setItemMeta(schuhe);
        builderGreater.add(hemd, 1);
        builderGreater.add(helm, 1);
        builderGreater.add(hose, 1);
        builderGreater.add(boots, 1);
        builderGreater.add(new ItemStack(Material.DIAMOND_CHESTPLATE, 1), 1);
        builderGreater.add(new ItemStack(Material.DIAMOND_HELMET, 1), 1);
        builderGreater.add(new ItemStack(Material.DIAMOND_LEGGINGS, 1), 1);
        builderGreater.add(new ItemStack(Material.DIAMOND_BOOTS, 1), 1);
        builderGreater.add(grenade, 50);
        builderGreater.add(sniper, 7);
        final ItemStack flashgrenade = new ItemStack(Material.DRAGON_EGG);
        final ItemMeta flashgrenademeta = flashgrenade.getItemMeta();
        flashgrenademeta.setDisplayName("§fFlashbang");
        flashgrenade.setItemMeta(flashgrenademeta);
        builderGreater.add(flashgrenade, 40);
        builderGreater.add(SKS.getItem(), 7);
        builderGreater.add(Scar.getItem(), 7);
        Main.lootGreater = builderGreater.build();
    }
    
    public void fillChest(final Inventory inventory, final LootWrapper wrapper) {
        final int slotDiff = wrapper.getMaxSlots() - wrapper.getMinSlots() + 1;
        final int slotCount = wrapper.getMinSlots() + this.random.nextInt(slotDiff);
        final ArrayList<Integer> exclusions = new ArrayList<Integer>();
        for (int i = 0; i < slotCount; ++i) {
            final int slot = this.generateRandom(0, inventory.getSize() - 1, exclusions);
            exclusions.add(slot);
            inventory.setItem(slot, Main.lootGreater.loot());
        }
    }
    
    private void preloadChunks() {
        final Location location = new Location(Bukkit.getWorld("battlegrounds"), 0.0, 0.0, 0.0);
        final int radius = 100;
        int startX = (int)location.getX() - (radius - 1) / 2 * 16;
        int startY = (int)location.getY() - (radius - 1) / 2 * 16;
        for (int y = 0; y < (radius - 1) / 2; ++y) {
            for (int x = 0; x < (radius - 1) / 2; ++x) {
                Bukkit.getWorld("battlegrounds").getChunkAt(new Location(Bukkit.getWorld("battlegrounds"), (double)startX, 0.0, (double)startY)).load();
                startX += 16;
            }
            startY += 16;
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
    
    public void addAchievement(final Player p, final Achievemnt ach) {
        final String playerName = p.getName();
        p.sendMessage(String.valueOf(Main.CHAT_PREFIX) + "You got a new Achievement!");
        p.sendMessage("§c§l" + ach.getName());
        p.sendMessage("§f§l" + ach.getDescription());
        this.getPlayersConfig().set("Players." + playerName + ".Achievement" + ach.getName(), (Object)true);
        this.savePlayersConfig();
    }
    
    public boolean getAchievement(final Player p, final Achievemnt ach) {
        final String playerName = p.getName();
        return this.getPlayersConfig().getBoolean("Players." + playerName + ".Achievement" + ach.getName());
    }
    
    public void reloadPlayersConfig() {
        if (this.customPlayersConfig == null) {
            this.customPlayersConfig = new File(this.f, "players.yml");
        }
        this.PlayersConfig = (FileConfiguration)YamlConfiguration.loadConfiguration(this.customPlayersConfig);
        final InputStream defConfigStream = this.getResource("players.yml");
        if (defConfigStream != null) {
            final YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            this.PlayersConfig.setDefaults((Configuration)defConfig);
        }
    }
    
    public FileConfiguration getPlayersConfig() {
        if (this.PlayersConfig == null) {
            this.reloadPlayersConfig();
        }
        return this.PlayersConfig;
    }
    
    public void savePlayersConfig() {
        if (this.PlayersConfig == null || this.customPlayersConfig == null) {
            return;
        }
        try {
            this.getPlayersConfig().save(this.customPlayersConfig);
        }
        catch (IOException ex) {
            System.out.println("Error");
        }
    }
    
    public Gun getExistingGun(final ItemStack item, final PUPlayer csplayer) {
        if (item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName() && !csplayer.getGuns().isEmpty()) {
            for (final Gun gun : csplayer.getGuns()) {
                if (gun.getName().equals(item.getItemMeta().getDisplayName())) {
                    return gun;
                }
            }
        }
        return null;
    }
    
    public boolean isGun(final ItemStack item) {
        if (item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            for (final String gunName : Main.gunNames) {
                if (gunName.equals(item.getItemMeta().getDisplayName())) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public void setupPlayersConfig() {
        this.reloadPlayersConfig();
        this.getPlayersConfig().options().copyDefaults(true);
        this.savePlayersConfig();
    }
    
    public PUPlayer getCSPlayer(final Player player) {
        for (final PUPlayer csPlayer : this.csPlayers) {
            if (csPlayer.getPlayer().getUniqueId().equals(player.getUniqueId())) {
                return csPlayer;
            }
        }
        return new PUPlayer(this, player);
    }
    
    public Collection<PUPlayer> getCSPlayers() {
        return this.csPlayers;
    }
    
    public void removePlayerFromFiring(final Player player, final int ticksDelay, final HashMap<UUID, Long> firing) {
        new BukkitRunnable() {
            public void run() {
                firing.remove(player.getUniqueId());
            }
        }.runTaskLater((Plugin)getInstance(), (long)ticksDelay);
    }
    
    public ItemStack getKnife() {
        final ItemStack knife = new ItemStack(Material.IRON_AXE);
        final ItemMeta meta = knife.getItemMeta();
        meta.setDisplayName(ChatColor.GRAY + "Standard Knife");
        knife.setItemMeta(meta);
        return knife;
    }
    
    public Connection getMySQLConnection() {
        try {
            final String host = "127.0.0.1";
            final String port = "3306";
            final String database = "s35_pubg";
            final String username = "s35_EMBTkUw6ZT";
            final String password = "kagH7uQa21oszKv92CJI";
            final String url = "jdbc:mysql://127.0.0.1:3306/s35_pubg";
            return DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/s35_pubg", "s35_EMBTkUw6ZT", "kagH7uQa21oszKv92CJI");
        }
        catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public int getKills(final Player p) {
        final String playerName = p.getName();
        if (this.useMySql()) {
            try {
                final Connection c = this.getMySQLConnection();
                final String prefix = Main.mysqlprefix;
                final Statement sql = c.createStatement();
                final ResultSet kills = sql.executeQuery("SELECT TotalKills FROM " + prefix + " WHERE player_name = '" + playerName + "';");
                kills.next();
                final int playerKills = kills.getInt("TotalKills");
                c.close();
                sql.close();
                kills.close();
                return playerKills;
            }
            catch (SQLException e) {
                e.printStackTrace();
                return 0;
            }
        }
        return this.getPlayersConfig().getInt("Players." + playerName + ".Kills");
    }
    
    public int getDeaths(final Player p) {
        final String playerName = p.getName();
        if (this.useMySql()) {
            try {
                final Connection c = this.getMySQLConnection();
                final String prefix = Main.mysqlprefix;
                final Statement sql = c.createStatement();
                final ResultSet deaths = sql.executeQuery("SELECT Deaths FROM " + prefix + " WHERE player_name = '" + playerName + "';");
                deaths.next();
                final int playerDeaths = deaths.getInt("Deaths");
                c.close();
                sql.close();
                deaths.close();
                return playerDeaths;
            }
            catch (SQLException e) {
                e.printStackTrace();
                return 0;
            }
        }
        return this.getPlayersConfig().getInt("Players." + playerName + ".Deaths");
    }
    
    public int getWins(final Player p) {
        final String playerName = p.getName();
        if (this.useMySql()) {
            try {
                final Connection c = this.getMySQLConnection();
                final String prefix = Main.mysqlprefix;
                final Statement sql = c.createStatement();
                final ResultSet wins = sql.executeQuery("SELECT Wins FROM " + prefix + " WHERE player_name = '" + playerName + "';");
                wins.next();
                final int playerWins = wins.getInt("Wins");
                c.close();
                sql.close();
                wins.close();
                return playerWins;
            }
            catch (SQLException e) {
                e.printStackTrace();
                return 0;
            }
        }
        return this.getPlayersConfig().getInt("Players." + playerName + ".Wins");
    }
    
    public int getTotalKills(final Player p) {
        final String playerName = p.getName();
        if (this.useMySql()) {
            try {
                final Connection c = this.getMySQLConnection();
                final String prefix = Main.mysqlprefix;
                final Statement sql = c.createStatement();
                final ResultSet coins = sql.executeQuery("SELECT TotalKills FROM " + prefix + " WHERE player_name = '" + playerName + "';");
                coins.next();
                final int playerCoins = coins.getInt("TotalKills");
                c.close();
                sql.close();
                coins.close();
                return playerCoins;
            }
            catch (SQLException e) {
                e.printStackTrace();
                return 0;
            }
        }
        return this.getPlayersConfig().getInt("Players." + playerName + ".TotalKills");
    }
    
    public void addTotalKills(final Player p, final int TotalKills) {
        final String playerName = p.getName();
        if (this.useMySql()) {
            try {
                final Connection c = this.getMySQLConnection();
                final String prefix = Main.mysqlprefix;
                final Statement sql = c.createStatement();
                final ResultSet playerTokens = sql.executeQuery("SELECT TotalKills FROM " + prefix + " WHERE player_name = '" + playerName + "';");
                playerTokens.next();
                final int lastTokens = playerTokens.getInt("TotalKills");
                final PreparedStatement updater = c.prepareStatement("UPDATE " + prefix + " SET TotalKills=? WHERE player_name = '" + playerName + "';");
                updater.setInt(1, lastTokens + TotalKills);
                updater.executeUpdate();
                c.close();
                updater.close();
                playerTokens.close();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else {
            this.getPlayersConfig().set("Players." + playerName + ".TotalKills", (Object)(this.getTotalKills(p) + TotalKills));
            this.savePlayersConfig();
        }
    }
    
    public void removeTotalKills(final Player p, final int Coins) {
        final String playerName = p.getName();
        if (this.useMySql()) {
            try {
                final Connection c = this.getMySQLConnection();
                final String prefix = Main.mysqlprefix;
                final Statement sql = c.createStatement();
                final ResultSet playerTokens = sql.executeQuery("SELECT TotalKills FROM " + prefix + " WHERE player_name = '" + playerName + "';");
                playerTokens.next();
                final int lastTokens = playerTokens.getInt("TotalKills");
                final PreparedStatement updater = c.prepareStatement("UPDATE " + prefix + " SET TotalKills=? WHERE player_name = '" + playerName + "';");
                updater.setInt(1, lastTokens - Coins);
                updater.executeUpdate();
                c.close();
                updater.close();
                playerTokens.close();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else {
            this.getPlayersConfig().set("Players." + playerName + ".TotalKills", (Object)(this.getTotalKills(p) - Coins));
            this.savePlayersConfig();
        }
    }
    
    public void setKills(final Player p, final int Coins) {
        final String playerName = p.getName();
        if (this.useMySql()) {
            try {
                final Connection c = this.getMySQLConnection();
                final String prefix = Main.mysqlprefix;
                final PreparedStatement updater = c.prepareStatement("UPDATE " + prefix + " SET Kills=? WHERE player_name = '" + playerName + "';");
                updater.setInt(1, Coins);
                updater.executeUpdate();
                c.close();
                updater.close();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else {
            this.getPlayersConfig().set("Players." + playerName + ".Kills", (Object)Coins);
            this.savePlayersConfig();
        }
    }
    
    public void setTotalKills(final Player p, final int Coins) {
        final String playerName = p.getName();
        if (this.useMySql()) {
            try {
                final Connection c = this.getMySQLConnection();
                final String prefix = Main.mysqlprefix;
                final PreparedStatement updater = c.prepareStatement("UPDATE " + prefix + " SET TotalKills=? WHERE player_name = '" + playerName + "';");
                updater.setInt(1, Coins);
                updater.executeUpdate();
                c.close();
                updater.close();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else {
            this.getPlayersConfig().set("Players." + playerName + ".TotalKills", (Object)Coins);
            this.savePlayersConfig();
        }
    }
    
    public void addKills(final Player p) {
        final String playerName = p.getName();
        if (this.useMySql()) {
            try {
                final Connection c = this.getMySQLConnection();
                final String prefix = Main.mysqlprefix;
                final Statement sql = c.createStatement();
                final ResultSet playerTokens = sql.executeQuery("SELECT Kills FROM " + prefix + " WHERE player_name = '" + playerName + "';");
                playerTokens.next();
                final int lastTokens = playerTokens.getInt("Kills");
                final PreparedStatement updater = c.prepareStatement("UPDATE " + prefix + " SET Kills=? WHERE player_name = '" + playerName + "';");
                updater.setInt(1, lastTokens + 1);
                updater.executeUpdate();
                c.close();
                updater.close();
                playerTokens.close();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else {
            this.getPlayersConfig().set("Players." + playerName + ".Kills", (Object)(this.getKills(p) + 1));
            this.savePlayersConfig();
        }
    }
    
    public void addDeath(final Player p) {
        final String playerName = p.getName();
        if (this.useMySql()) {
            try {
                final Connection c = this.getMySQLConnection();
                final String prefix = Main.mysqlprefix;
                final Statement sql = c.createStatement();
                final ResultSet playerTokens = sql.executeQuery("SELECT Deaths FROM " + prefix + " WHERE player_name = '" + playerName + "';");
                playerTokens.next();
                final int lastTokens = playerTokens.getInt("Deaths");
                final PreparedStatement updater = c.prepareStatement("UPDATE " + prefix + " SET Deaths=? WHERE player_name = '" + playerName + "';");
                updater.setInt(1, lastTokens + 1);
                updater.executeUpdate();
                c.close();
                updater.close();
                playerTokens.close();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else {
            this.getPlayersConfig().set("Players." + playerName + ".Deaths", (Object)(this.getDeaths(p) + 1));
            this.savePlayersConfig();
        }
    }
    
    public boolean useMySql() {
        return false;
    }
    
    public void addWins(final Player p) {
        final String playerName = p.getName();
        if (this.useMySql()) {
            try {
                final Connection c = this.getMySQLConnection();
                final String prefix = Main.mysqlprefix;
                final Statement sql = c.createStatement();
                final ResultSet playerTokens = sql.executeQuery("SELECT Wins FROM " + prefix + " WHERE player_name = '" + playerName + "';");
                playerTokens.next();
                final int lastTokens = playerTokens.getInt("Wins");
                final PreparedStatement updater = c.prepareStatement("UPDATE " + prefix + " SET Wins=? WHERE player_name = '" + playerName + "';");
                updater.setInt(1, lastTokens + 1);
                updater.executeUpdate();
                c.close();
                updater.close();
                playerTokens.close();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else {
            this.getPlayersConfig().set("Players." + playerName + ".Wins", (Object)(this.getWins(p) + 1));
            this.savePlayersConfig();
        }
    }
    
    public void createTable() throws SQLException {
        try {
            final String prefix = Main.mysqlprefix;
            final Connection c = this.getMySQLConnection();
            final Statement statement = c.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + prefix + " (player_name VARCHAR(40), TotalKills int, Kills int, Deaths int, Wins int)");
            statement.close();
            c.close();
            System.out.println(String.valueOf(Main.CHAT_PREFIX) + "Sucessfully connected to database");
        }
        catch (SQLException e) {
            System.out.println(String.valueOf(Main.CHAT_PREFIX) + "Couldnt connect to database");
        }
    }
}
