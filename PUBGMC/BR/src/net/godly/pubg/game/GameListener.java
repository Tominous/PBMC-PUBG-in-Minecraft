package net.godly.pubg.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

import net.godly.pubg.Main;
import net.godly.pubg.game.achievment.Berserker;
import net.godly.pubg.game.achievment.DestroyFlower;
import net.godly.pubg.game.event.BulletHitEvent;
import net.godly.pubg.game.event.LobbyStatsType;
import net.godly.pubg.game.event.LobbyStatsUpdateEvent;
import net.godly.pubg.game.event.WeaponFireEvent;
import net.godly.pubg.game.event.update.UpdateEvent;
import net.godly.pubg.game.event.update.UpdateType;
import net.godly.pubg.game.guns.Gun;
import net.godly.pubg.game.guns.runnables.Gunfire;
import net.godly.pubg.game.timer.StartTimer;
import net.godly.pubg.game.timer.TimeHelper;
import net.godly.pubg.player.PUPlayer;
import net.godly.pubg.utils.PlayerUtils;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayInClientCommand;

public class GameListener implements Listener
{
    public static ArrayList<Player> livingplayers;
    private HashMap<UUID, Long> firing;
    private Set<UUID> pistolCD;
    HashMap<UUID, Integer> kills;
    StartTimer ts;
    final String CHAT_PREFIX;
    Random gRandom;
    private List<String> cooldown;
    private HashMap<Entity, BukkitRunnable> entRun;
    int max;
    public List<String> zoom;
    public Main plugin;
    int timeto;
    
    static {
        GameListener.livingplayers = new ArrayList<Player>();
    }
    
    public GameListener() {
        this.kills = new HashMap<UUID, Integer>();
        this.ts = new StartTimer(Main.getInstance());
        this.CHAT_PREFIX = Main.CHAT_PREFIX;
        this.gRandom = new Random();
        this.cooldown = new ArrayList<String>();
        this.entRun = new HashMap<Entity, BukkitRunnable>();
        this.max = 20;
        this.zoom = new ArrayList<String>();
        this.plugin = Main.getInstance();
        this.timeto = 50;
        this.pistolCD = new HashSet<UUID>();
        this.firing = new HashMap<UUID, Long>();
    }
    
    @EventHandler
    public void onStartTimerFullSecond(final StartTimerFullSecondAndRunningEvent e) {
        if (e.getSecondsLeft() == 10) {
            Main.getInstance().getGameStateManager().setState(GameState.RUNNING_GRACEPERIOD);
            for (Player player2 : Bukkit.getOnlinePlayers()) {}
        }
        else if (e.getSecondsLeft() == 5) {
            for (final Player player : Bukkit.getOnlinePlayers()) {
                player.getInventory().clear();
                final Random rd = new Random();
                final int x = rd.nextInt(500);
                final int z = rd.nextInt(500);
                final double y = Bukkit.getWorld(Main.getInstance().worldName).getHighestBlockYAt(x, z);
                player.teleport(new Location(Bukkit.getWorld(Main.getInstance().worldName), (double)x, y, (double)z, 180.0f, 0.0f));
            }
        }
        else if (e.getSecondsLeft() == 0) {
            for (final Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(String.valueOf(Main.CHAT_PREFIX) + "The game has started, good luck!");
            }
        }
    }
    
    @EventHandler
    public void onJoin(final PlayerJoinEvent e) {
        /*if (Bukkit.getOnlinePlayers().size() >= Main.instance.minplayers) {
            if (this.ts.timerRunning) {
                return;
            }
            this.ts.runTaskTimer((Plugin)Main.getInstance(), 0L, 1L);
        }*/
        final Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();
        final Objective o = sb.registerNewObjective("scoreboard", "dummy");
        o.setDisplaySlot(DisplaySlot.SIDEBAR);
        o.setDisplayName(new StringBuilder(String.valueOf(Main.ScoreBoardTitle)).toString());
        o.getScore(new StringBuilder().append(ChatColor.RED).toString()).setScore(15);
        o.getScore(new StringBuilder().append(ChatColor.WHITE).append(ChatColor.BOLD).append("Your Name:").toString()).setScore(14);
        o.getScore(ChatColor.RED + e.getPlayer().getName()).setScore(13);
        o.getScore(ChatColor.RED + " ").setScore(12);
        o.getScore(new StringBuilder().append(ChatColor.WHITE).append(ChatColor.BOLD).append("Kills:").toString()).setScore(8);
        o.getScore(ChatColor.YELLOW + "loading....").setScore(7);
        new BukkitRunnable() {
            public void run() {
                sb.resetScores(ChatColor.YELLOW + "loading....");
                o.getScore(new StringBuilder().append(ChatColor.YELLOW).append(Main.getInstance().getKills(e.getPlayer())).toString()).setScore(7);
            }
        }.runTaskLater((Plugin)Main.getInstance(), 40L);
        o.getScore(ChatColor.RED + "   ").setScore(6);
        o.getScore(Main.ServerName).setScore(2);
        e.getPlayer().setScoreboard(sb);
        Main.getInstance().getCSPlayers().add(new PUPlayer(Main.getInstance(), e.getPlayer()));
     if(new Location(Bukkit.getWorld(Main.getInstance().worldName), Main.getInstance().SpawnX, Main.getInstance().SpawnY, Main.getInstance().SpawnZ) != null) {
    	 e.getPlayer().teleport(new Location(Bukkit.getWorld(Main.getInstance().worldName), Main.getInstance().SpawnX, Main.getInstance().SpawnY, Main.getInstance().SpawnZ));
     }
        GameListener.livingplayers.add(e.getPlayer());
    }
    
    public int getPing(final Player player) {
        final CraftPlayer cp = (CraftPlayer)player;
        final EntityPlayer ep = cp.getHandle();
        return ep.ping;
    }
    
    @EventHandler
    public void onJoin(final PlayerPreLoginEvent e) {
        if (Main.getInstance().gameStateManager.getState() == GameState.RUNNING) {
            e.disallow(PlayerPreLoginEvent.Result.KICK_OTHER, "The game is already running!");
        }
    }
    
    @EventHandler
    public void onQuit(final PlayerQuitEvent e) {
        final PUPlayer pi = Main.getInstance().getCSPlayer(e.getPlayer());
        Main.getInstance().getCSPlayers().remove(pi);
        final Player p = e.getPlayer();
        if (GameListener.livingplayers.contains(p)) {
            GameListener.livingplayers.remove(e.getPlayer());
        }
        e.setQuitMessage("");
        if (Main.getInstance().gameStateManager.getState().getRoot() == GameState.RUNNING) {
            if (Main.getInstance().getKills(p) != 0) {
                Main.getInstance().setTotalKills(p, Main.getInstance().getKills(p) + Main.getInstance().getTotalKills(p));
            }
            Main.getInstance().setKills(p, 0);
            p.getInventory().clear();
            p.setHealth(0.0);
            Bukkit.getServer().broadcastMessage(String.valueOf(this.CHAT_PREFIX) + p.getName() + ChatColor.GRAY + " left the Game!");
            ItemStack[] contents;
            for (int length = (contents = p.getInventory().getContents()).length, i = 0; i < length; ++i) {
                final ItemStack stack = contents[i];
                if (stack != null && stack.getType() != Material.AIR) {
                    p.getLocation().getWorld().dropItem(p.getLocation(), stack);
                    p.getInventory().remove(stack);
                }
            }
            ItemStack[] armorContents;
            for (int length2 = (armorContents = p.getInventory().getArmorContents()).length, j = 0; j < length2; ++j) {
                final ItemStack stack = armorContents[j];
                if (stack != null && stack.getType() != Material.AIR) {
                    p.getLocation().getWorld().dropItem(p.getLocation(), stack);
                    p.getInventory().remove(stack);
                }
            }
            if (GameListener.livingplayers.size() > 1) {
                Bukkit.getServer().broadcastMessage(String.valueOf(this.CHAT_PREFIX) + GameListener.livingplayers.size() + " Players are remaining.");
            }
            else {
                final Iterator<Player> iterator = GameListener.livingplayers.iterator();
                if (iterator.hasNext()) {
                    final Player winner;
                    final Player uuid = winner = iterator.next();
                    if (winner != null) {
                        Bukkit.getServer().broadcastMessage(String.valueOf(this.CHAT_PREFIX) + ChatColor.AQUA + winner.getName() + ChatColor.GREEN + " won the Game with " + ChatColor.RED + (int)(winner.getHealth() / 2.0) + " \u2764 " + ChatColor.GREEN + "remaining health!");
                        winner.setHealth(20.0);
                        winner.setFoodLevel(20);
                        Main.getInstance().setTotalKills(winner, Main.getInstance().getKills(p) + Main.getInstance().getTotalKills(winner));
                        Main.getInstance().setKills(winner, 0);
                    }
                    else {
                        Bukkit.getServer().broadcastMessage(String.valueOf(this.CHAT_PREFIX) + ChatColor.AQUA + "Nobody has won, ending the game!");
                        Bukkit.getServer().broadcastMessage(String.valueOf(Main.CHAT_PREFIX) + ChatColor.RED + "The Server will restart in 10 seconds");
                    }
                }
                this.end();
            }
        }
        else {
            Bukkit.getServer().broadcastMessage(String.valueOf(this.CHAT_PREFIX) + p.getName() + ChatColor.GRAY + " left the Game!");
        }
    }
    
    @EventHandler
    public void onPlayerRespawn(final PlayerRespawnEvent e) {
        final Player p = e.getPlayer();
        boolean spectator = false;
        switch (Main.getInstance().getGameStateManager().getState().getRoot()) {
            default: {
                e.setRespawnLocation(Bukkit.getWorld(Main.getInstance().worldName).getSpawnLocation());
                break;
            }
            case RUNNING: {
                spectator = true;
                e.setRespawnLocation(Bukkit.getWorld(Main.getInstance().worldName).getSpawnLocation());
                break;
            }
            case CLEANUP: {
                spectator = true;
                e.setRespawnLocation(Bukkit.getWorld(Main.getInstance().worldName).getSpawnLocation());
                break;
            }
        }
        final boolean isSpec = spectator;
        new BukkitRunnable() {
            public void run() {
                if (p.getFireTicks() > 0) {
                    p.setFireTicks(0);
                }
                if (isSpec) {
                    p.setGameMode(GameMode.SPECTATOR);
                }
            }
        }.runTaskLater((Plugin)Main.getInstance(), 1L);
    }
    
    @EventHandler
    public void onPlayerDeath(final PlayerDeathEvent e) {
        e.setDeathMessage("");
        final Player p = e.getEntity();
        if (p.getKiller() != null && p.getKiller().getType() == EntityType.LIGHTNING) {
            Bukkit.getServer().broadcastMessage(String.valueOf(Main.CHAT_PREFIX) + ChatColor.AQUA + p.getName() + ChatColor.RED + " was killed by " + ChatColor.RED + "a Lightning !");
        }
        if (Main.getInstance().getGameStateManager().getState().getRoot() != GameState.RUNNING) {
            e.getDrops().clear();
            e.setDroppedExp(0);
        }
        else if (GameListener.livingplayers.contains(p)) {
            if (Main.getInstance().getKills(p) != 0) {
                Main.getInstance().setTotalKills(p, Main.getInstance().getKills(p) + Main.getInstance().getTotalKills(p));
            }
            Main.getInstance().setKills(p, 0);
            final Player p2 = e.getEntity().getKiller();
            if (p2 != null && p2 instanceof Player) {
                Bukkit.getServer().broadcastMessage(String.valueOf(Main.CHAT_PREFIX) + ChatColor.AQUA + p.getName() + ChatColor.RED + " was killed by " + ChatColor.AQUA + p2.getName() + ChatColor.RED + (p.getItemInHand().hasItemMeta() ? ("using " + p.getItemInHand().getItemMeta().getDisplayName()) : "") + " !");
                Main.getInstance().addKills(p2);
                Main.getInstance().addDeath(p);
                if (Main.getInstance().getKills(p2) >= 5 && !Main.getInstance().getAchievement(p2, new Berserker())) {
                    Main.getInstance().addAchievement(p2, new Berserker());
                }
                Bukkit.getServer().getPluginManager().callEvent((Event)new LobbyStatsUpdateEvent(p2.getUniqueId(), LobbyStatsType.KILLS, Main.getInstance().getKills(p2) - 1, Main.getInstance().getKills(p2)));
                Main.getInstance().addTotalKills(p2, 1);
                if (GameListener.livingplayers.contains(p2)) {
                    p2.playSound(p2.getLocation(), Sound.WITHER_DEATH, 0.5f, 0.75f);
                }
            }
            else {
                Bukkit.getServer().broadcastMessage(String.valueOf(Main.CHAT_PREFIX) + ChatColor.AQUA + p.getName() + ChatColor.RED + " died!");
            }
            final TimeHelper time = new TimeHelper();
            GameListener.livingplayers.remove(p);
        }
        new BukkitRunnable() {
            public void run() {
                ((CraftPlayer)p).getHandle().playerConnection.a(new PacketPlayInClientCommand(PacketPlayInClientCommand.EnumClientCommand.PERFORM_RESPAWN));
            }
        }.runTaskLater((Plugin)Main.getInstance(), 1L);
        if (GameListener.livingplayers.size() > 1) {
            Bukkit.getServer().broadcastMessage(String.valueOf(Main.CHAT_PREFIX) + GameListener.livingplayers.size() + " Players are remaining.");
        }
        else if (GameListener.livingplayers.size() == 1) {
            for (int i = 0; i < GameListener.livingplayers.size(); ++i) {
                final Player winner = GameListener.livingplayers.get(i);
                Bukkit.getServer().broadcastMessage(String.valueOf(Main.CHAT_PREFIX) + ChatColor.AQUA + winner.getName() + ChatColor.GREEN + " won the Game with " + ChatColor.RED + (int)(winner.getHealth() / 2.0) + " \u2764 " + ChatColor.GREEN + "remaining health and now eats his Winner Chicken Dinner!");
                winner.setFoodLevel(20);
                Bukkit.getServer().broadcastMessage(String.valueOf(Main.CHAT_PREFIX) + ChatColor.RED + "The Server will restart in 10 seconds");
                this.end();
            }
        }
    }
    
    @EventHandler
    public void onPlayerInteract(final PlayerInteractEvent e) {
        if ((Main.getInstance().getGameStateManager().getState() == GameState.RUNNING || Main.getInstance().getGameStateManager().getState() == GameState.RUNNING_GRACEPERIOD) && GameListener.livingplayers.contains(e.getPlayer()) && (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) && e.getItem() != null && e.getItem().getType() == Material.COMPASS) {
            Player nearestPlayer = null;
            double lastDistance = Double.MAX_VALUE;
            final Location pLoc = e.getPlayer().getLocation();
            pLoc.setY(0.0);
            for (final Player otherP : Bukkit.getServer().getOnlinePlayers()) {
                if (otherP.getUniqueId() != e.getPlayer().getUniqueId() && GameListener.livingplayers.contains(otherP)) {
                    final Location loc1 = otherP.getLocation();
                    loc1.setY(0.0);
                    final double dist = loc1.distanceSquared(pLoc);
                    if (dist >= lastDistance) {
                        continue;
                    }
                    nearestPlayer = otherP;
                    lastDistance = dist;
                }
            }
            if (nearestPlayer != null) {
                e.getPlayer().sendMessage(String.valueOf(Main.CHAT_PREFIX) + ChatColor.AQUA + "The next player is " + (int)Math.sqrt(lastDistance) + "m away!");
            }
        }
    }
    
    @EventHandler
    public void onAutoSmelt(final BlockBreakEvent e) {
        if (e.getBlock().getType().equals((Object)Material.IRON_ORE)) {
            e.getBlock().setType(Material.AIR);
            e.getPlayer().getInventory().addItem(new ItemStack[] { new ItemStack(Material.IRON_INGOT) });
        }
    }
    
    @EventHandler
    public void onPlayerInteract1(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final World world = player.getWorld();
        final double speedFactor = 1.5;
        final Location handLocation = player.getLocation();
        handLocation.setY(handLocation.getY() + 1.0);
        final Vector direction = handLocation.getDirection();
        Entity entity = null;
        if (event.getMaterial() == Material.TNT && event.getAction() == Action.LEFT_CLICK_AIR) {
            if (player.getItemInHand().getAmount() == 1) {
                player.setItemInHand((ItemStack)null);
            }
            else {
                final ItemStack m = player.getItemInHand();
                m.setAmount(player.getItemInHand().getAmount() - 1);
                player.setItemInHand(m);
            }
            entity = world.spawn(handLocation, (Class)TNTPrimed.class);
            entity.setVelocity(direction.multiply(1.5));
        }
    }
    
    public static void throwGrenade(final Player player) {
        final World world = player.getWorld();
        final double speedFactor = 1.5;
        final Location handLocation = player.getLocation();
        handLocation.setY(handLocation.getY() + 1.0);
        final Vector direction = handLocation.getDirection();
        Entity entity = null;
        final boolean perms = player.isOp() || player.hasPermission("grenade.throw");
        if (perms) {
            entity = world.spawn(handLocation, (Class)TNTPrimed.class);
            entity.setVelocity(direction.multiply(1.5));
        }
    }
    
    public void end() {
        final boolean restart = Main.getInstance().cfg.getBoolean("RestartServerAfterMatch");
        if (!restart) {
            return;
        }
        new BukkitRunnable() {
            public void run() {
                for (final Player p : Bukkit.getOnlinePlayers()) {
                    Main.getInstance().setKills(p, 0);
                    Bukkit.getServer().shutdown();
                }
            }
        }.runTaskLater((Plugin)Main.getInstance(), 200L);
    }
    
    @EventHandler
    public void onShoot(final PlayerInteractEvent e) {
        final Player p = e.getPlayer();
        if (Main.getInstance().isGun(p.getItemInHand()) && (e.getAction() == Action.LEFT_CLICK_AIR | e.getAction() == Action.LEFT_CLICK_BLOCK)) {
            p.playSound(p.getLocation(), Sound.IRONGOLEM_HIT, 3.0f, 3.0f);
            if (this.zoom.contains(p.getName())) {
                this.zoom.remove(p.getName());
                p.removePotionEffect(PotionEffectType.SLOW);
            }
            else {
                this.zoom.add(p.getName());
                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 3));
            }
        }
    }
    
    @EventHandler
    public void playerInteractEvent(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if ((event.getAction().equals((Object)Action.RIGHT_CLICK_AIR) || event.getAction().equals((Object)Action.RIGHT_CLICK_BLOCK)) && Main.getInstance().isGun(player.getItemInHand())) {
            final PUPlayer csplayer = Main.getInstance().getCSPlayer(player);
            final Gun gun = Main.getInstance().getExistingGun(player.getItemInHand(), csplayer);
            if (gun == null) {
                return;
            }
            if (gun.hasAutoFire()) {
                Main.getInstance().removePlayerFromFiring(player, 5, this.firing);
                if (gun.getAmmunition() <= 0) {
                    return;
                }
                this.firing.put(player.getUniqueId(), System.currentTimeMillis());
                new Gunfire(this.firing, player, gun).runTaskTimer((Plugin)Main.getInstance(), 0L, 6L);
            }
            else if (gun.isShotGun()) {
                this.shootShot(player, gun);
            }
            else {
                this.shoot(player, gun);
            }
        }
    }
    
    private void shoot(final Player player, final Gun gun) {
        if (this.pistolCD.contains(player.getUniqueId())) {
            return;
        }
        if (gun.getAmmunition() <= 0) {
            return;
        }
        final Vector playerDir = player.getLocation().getDirection();
        final Vector dirVel = playerDir.multiply(5);
        final Snowball bullet = (Snowball)player.getWorld().spawnEntity(PlayerUtils.getRightHeadLocation((LivingEntity)player), EntityType.SNOWBALL);
        bullet.setVelocity(dirVel);
        bullet.setShooter((ProjectileSource)player);
        bullet.setBounce(false);
        player.getWorld().playSound(player.getLocation(), Sound.ENDERDRAGON_HIT, 1.0f, 1.0f);
        gun.setAmmunition(gun.getAmmunition() - 1);
        final WeaponFireEvent calledEvent = new WeaponFireEvent(player, gun, (Projectile)bullet);
        Bukkit.getPluginManager().callEvent((Event)calledEvent);
        this.pistolCD.add(player.getUniqueId());
        new BukkitRunnable() {
            public void run() {
                GameListener.this.pistolCD.remove(player.getUniqueId());
            }
        }.runTaskLater((Plugin)Main.getInstance(), 5L);
    }
    
    private void shootShot(final Player player, final Gun gun) {
        if (this.pistolCD.contains(player.getUniqueId())) {
            return;
        }
        if (gun.getAmmunition() <= 0) {
            return;
        }
        final Vector playerDir = player.getLocation().getDirection();
        final Vector dirVel = playerDir.multiply(5);
        final Snowball bullet = (Snowball)player.getWorld().spawnEntity(PlayerUtils.getRightHeadLocation((LivingEntity)player), EntityType.SNOWBALL);
        bullet.setVelocity(dirVel);
        bullet.setShooter((ProjectileSource)player);
        bullet.setBounce(false);
        player.getWorld().playSound(player.getLocation(), Sound.ENDERDRAGON_HIT, 1.0f, 1.0f);
        gun.setAmmunition(gun.getAmmunition() - 1);
        final WeaponFireEvent calledEvent = new WeaponFireEvent(player, gun, (Projectile)bullet);
        Bukkit.getPluginManager().callEvent((Event)calledEvent);
    }
    
    @EventHandler
    public void onFlashBangY(final PlayerInteractEvent e) {
        if ((e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) && e.getMaterial().equals((Object)Material.DRAGON_EGG)) {
            final Player p = e.getPlayer();
            e.setCancelled(true);
            final Ocelot horse = (Ocelot)p.getWorld().spawnEntity(p.getLocation(), EntityType.OCELOT);
            horse.setVelocity(p.getLocation().getDirection().multiply(3.0));
            if (p.getItemInHand().getAmount() == 1) {
                p.setItemInHand((ItemStack)null);
            }
            else {
                final ItemStack m = p.getItemInHand();
                m.setAmount(p.getItemInHand().getAmount() - 1);
                p.setItemInHand(m);
            }
            new BukkitRunnable() {
                public void run() {
                    for (final Entity ent : horse.getNearbyEntities(2.0, 2.0, 2.0)) {
                        if (ent.getType() == EntityType.PLAYER) {
                            final Player damage = (Player)ent;
                            damage.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 2));
                            damage.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 100, 2));
                            damage.getWorld().strikeLightningEffect(damage.getLocation());
                            damage.getWorld().playEffect(damage.getLocation(), Effect.FIREWORKS_SPARK, 3);
                            p.getWorld().playSound(p.getLocation(), Sound.FIREWORK_LARGE_BLAST2, 3.0f, 3.0f);
                            p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20, 3));
                            horse.remove();
                        }
                    }
                }
            }.runTaskTimer((Plugin)this.plugin, 10L, 10L);
        }
    }
    
    @EventHandler
    public void onBlockBreakEvennt(final BlockBreakEvent e) {
        final Player p = e.getPlayer();
        if ((e.getBlock().getType().equals((Object)Material.YELLOW_FLOWER) || e.getBlock().getType().equals((Object)Material.RED_ROSE)) && !Main.getInstance().getAchievement(p, new DestroyFlower())) {
            Main.getInstance().addAchievement(p, new DestroyFlower());
        }
    }
    
    @EventHandler
    public void entityDamageByEntityEvent(final EntityDamageByEntityEvent event) {
        if (Main.getInstance().getGameStateManager().getState().getRoot() != GameState.RUNNING) {
            event.setDamage(0.0);
            event.setCancelled(true);
        }
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            final Player damager = (Player)event.getDamager();
            final Player victim = (Player)event.getEntity();
            if (damager.getItemInHand().equals((Object)Main.getInstance().getKnife()) && damager.getItemInHand().hasItemMeta()) {
                System.out.println("Has knife in hand!!!");
                if (PlayerUtils.playerBehindPlayer(damager, victim)) {
                    event.setDamage(20.0);
                }
            }
        }
        if (event.getDamager() instanceof Snowball && event.getEntity() instanceof Player) {
            final Snowball bullet = (Snowball)event.getDamager();
            if (bullet.getShooter() instanceof Player) {
                final Player damager2 = (Player)bullet.getShooter();
                final Player victim2 = (Player)event.getEntity();
                if (Main.getInstance().isGun(damager2.getItemInHand())) {
                    final PUPlayer csplayer = Main.getInstance().getCSPlayer(damager2);
                    final Gun gun = Main.getInstance().getExistingGun(damager2.getItemInHand(), csplayer);
                    final BulletHitEvent calledEvent = new BulletHitEvent(damager2, victim2, (Projectile)bullet, gun);
                    Bukkit.getPluginManager().callEvent((Event)calledEvent);
                    victim2.setNoDamageTicks(0);
                    event.setCancelled(true);
                }
            }
        }
    }
    
    @EventHandler
    public void onUpdate(final UpdateEvent e) {
        if (e.getType() == UpdateType.SEC) {
            final TimeHelper time = new TimeHelper();
            if (Main.getInstance().gameStateManager.getState() == GameState.RUNNING) {
            	if(Bukkit.getOnlinePlayers().size() == 0) {
            		this.end();
            	}
                for (final Player p : Bukkit.getOnlinePlayers()) {
                    if (!p.isOnGround()) {
                        return;
                    }
                   
                    if (this.timeto == 0) {
                        new BukkitRunnable() {
                            public void run() {
                                Bukkit.broadcastMessage(String.valueOf(Main.CHAT_PREFIX) + "Shrinking Border now Seconds to 200 blocks");
                                Bukkit.getWorld("battlegrounds").getWorldBorder().setSize(400.0);
                                GameListener.this.timeto = 50;
                            }
                        }.runTaskLater((Plugin)Main.getInstance(), 50000L);
                    }
                    if (this.timeto == 0) {
                        return;
                    }
                    Bukkit.broadcastMessage(String.valueOf(Main.CHAT_PREFIX) + "Shrinking Border in " + this.timeto + " Seconds to 200 blocks");
                    --this.timeto;
                    if (GameListener.livingplayers.size() >= 10) {
                        new BukkitRunnable() {
                            public void run() {
                                Bukkit.broadcastMessage(String.valueOf(Main.CHAT_PREFIX) + "Shrinking Border " + GameListener.this.timeto + " Seconds to 300 blocks");
                            }
                        }.runTaskLater((Plugin)Main.getInstance(), 50000L);
                        new BukkitRunnable() {
                            public void run() {
                                Bukkit.broadcastMessage(String.valueOf(Main.CHAT_PREFIX) + "Shrinking Border now to 300 blocks");
                                Bukkit.getWorld("battlegrounds").getWorldBorder().setSize(600.0);
                            }
                        }.runTaskLater((Plugin)Main.getInstance(), 50000L);
                    }
                    else {
                        if (GameListener.livingplayers.size() < 20) {
                            continue;
                        }
                        new BukkitRunnable() {
                            public void run() {
                                Bukkit.broadcastMessage(String.valueOf(Main.CHAT_PREFIX) + "Shrinking Border " + GameListener.this.timeto + " Seconds to 400 blocks");
                            }
                        }.runTaskLater((Plugin)Main.getInstance(), 50000L);
                        new BukkitRunnable() {
                            public void run() {
                                Bukkit.broadcastMessage(String.valueOf(Main.CHAT_PREFIX) + "Shrinking Border now to 400 blocks");
                                Bukkit.getWorld("battlegrounds").getWorldBorder().setSize(800.0);
                            }
                        }.runTaskLater((Plugin)Main.getInstance(), 50000L);
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onBlockBreakEvent(final BlockBreakEvent e) {
        if (Main.getInstance().getGameStateManager().getState().getRoot() == GameState.WAITING) {
            e.setCancelled(true);
        }
    }
}
