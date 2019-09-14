package net.aluix.pubg.game.timer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import net.aluix.pubg.Main;
import net.aluix.pubg.game.GameListener;
import net.aluix.pubg.game.GameState;

public class StartTimer extends BukkitRunnable {
	private Main pluginInstance;
	private static final int countdownTime = 1201;
	public boolean timerRunning;
	private long ticksCounted;
	public int ticksLeft;
	public int graceTicksLeft;
	private Server serverInstance;

	public StartTimer(final Main pluginInstance) {
		this.timerRunning = false;
		this.ticksCounted = 0L;
		this.ticksLeft = 1201;
		this.graceTicksLeft = 1500;
		this.pluginInstance = pluginInstance;
		this.serverInstance = pluginInstance.getServer();
	}
	@Override
	public void run() {
		++this.ticksCounted;
		if (this.ticksCounted >= 9223372036854775756L) {
			this.ticksCounted = 0L;
		}
		if (this.pluginInstance.getGameStateManager().getState().equals(GameState.WAITING)) {
			if (!this.timerRunning) {
				if (Bukkit.getOnlinePlayers().size() >= Main.getInstance().minplayers) {
					this.timerRunning = true;
					System.out.println("xDDDD");
				} else if (this.ticksCounted % 200L == 0L && Bukkit.getOnlinePlayers().size() > 0) {
					this.serverInstance.broadcastMessage(String.valueOf(this.pluginInstance.getChatPrefix())
							+ ChatColor.RED + "Waiting for more players... (" + Bukkit.getOnlinePlayers().size() + "/"
							+ Main.getInstance().minplayers + ")");
				}
			} else {
				if (Bukkit.getOnlinePlayers().size() < Main.getInstance().minplayers) {
					this.timerRunning = false;
					this.ticksLeft = 1201;
					return;
				}
				--this.ticksLeft;
				if (this.ticksLeft <= 0) {
					final Location zeroPoint = new Location(Bukkit.getWorld("battlegrounds"), 0.0, 200.0, 0.0);
					final Random random = new Random();
					final ItemStack tracker = new ItemStack(Material.COMPASS, 1);
					final ItemMeta trackerMeta = tracker.getItemMeta();
					trackerMeta.setDisplayName(ChatColor.YELLOW + "Tracer");
					final ArrayList<String> trackerLore = new ArrayList<String>();
					trackerLore.add(ChatColor.GRAY + "Shows where to walk at to find another player!");
					trackerLore.add(ChatColor.GRAY + "Rightclick to see the distance to another player!");
					trackerMeta.setLore((List) trackerLore);
					tracker.setItemMeta(trackerMeta);
					for (final Player player : Bukkit.getOnlinePlayers()) {
						player.getInventory().clear();
						player.getInventory().addItem(new ItemStack[] { tracker });
						final ItemStack parachute = new ItemStack(Material.BANNER);
						final ItemMeta meta = parachute.getItemMeta();
						meta.setDisplayName("§6§lParachute");
						parachute.setItemMeta(meta);
						player.getInventory().addItem(new ItemStack[] { parachute });
						final Random rd = new Random();
						final int x = rd.nextInt(500);
						final int z = rd.nextInt(500);
						final double y = Bukkit.getWorld("battlegrounds").getHighestBlockYAt(x, z);
						player.teleport(new Location(Bukkit.getWorld("battlegrounds"), (double) x, 256.0, (double) z,
								180.0f, 0.0f));
						GameListener.livingplayers.add(player);
						player.setHealth(20.0);
						player.setFoodLevel(20);
					}
					this.serverInstance.broadcastMessage(
							String.valueOf(Main.CHAT_PREFIX) + ChatColor.GREEN + "The game has begun, Good luck!");
					Bukkit.getWorld("battlegrounds").setPVP(true);
					this.pluginInstance.gameStateManager.setState(GameState.RUNNING);
				} else if (this.ticksLeft % 200 == 0 || (this.ticksLeft < 200 && this.ticksLeft % 20 == 0)) {
					if (this.ticksLeft == 20) {
						this.serverInstance.broadcastMessage(String.valueOf(Main.CHAT_PREFIX) + ChatColor.AQUA + "1"
								+ ChatColor.GRAY + " Seconds left until the game starts!");
					}else if(this.ticksLeft == 800) {
						Main.getInstance().fill();
					}else {
						
						this.serverInstance.broadcastMessage(String.valueOf(Main.CHAT_PREFIX) + ChatColor.AQUA
								+ this.ticksLeft / 20 + ChatColor.GRAY + " Seconds left until the game starts!");
					}
					for (int length = Bukkit.getOnlinePlayers().size(), i = 0; i < length; ++i) {
						final Player p = (Player) Bukkit.getOnlinePlayers().toArray()[i];
						p.playSound(p.getEyeLocation(), Sound.NOTE_BASS, 1.0f, 1.0f);
					}
				}
			}
		}

	}

}
