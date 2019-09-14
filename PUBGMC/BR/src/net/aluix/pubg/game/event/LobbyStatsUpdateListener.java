package net.aluix.pubg.game.event;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scoreboard.DisplaySlot;

import net.aluix.pubg.Main;
import net.md_5.bungee.api.ChatColor;

public class LobbyStatsUpdateListener implements Listener
{
    @EventHandler
    public void onLobbyStatsUpdate(final LobbyStatsUpdateEvent e) {
        final Player p = Main.getInstance().getServer().getPlayer(e.getPlayer());
        if (p != null && p.isOnline() && e.getType() == LobbyStatsType.KILLS) {
            p.getScoreboard().resetScores(new StringBuilder().append(ChatColor.YELLOW).append(e.getBefore()).toString());
            p.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore(new StringBuilder().append(ChatColor.YELLOW).append(e.getNow()).toString()).setScore(7);
        }
    }
}
