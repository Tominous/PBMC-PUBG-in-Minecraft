package net.aluix.pubg.game;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.aluix.pubg.Main;
import net.md_5.bungee.api.ChatColor;

public class CommandStart implements CommandExecutor
{
    private Main pluginInstance;
    
    public CommandStart(final Main pluginInstance) {
        this.pluginInstance = pluginInstance;
    }
    
    public boolean onCommand(final CommandSender commandSender, final Command command, final String s, final String[] strings) {
        if (commandSender instanceof Player) {
            final Player p = (Player)commandSender;
            p.sendMessage(String.valueOf(Main.CHAT_PREFIX) + "Wins: " + Main.getInstance().getWins(p));
            p.sendMessage(String.valueOf(Main.CHAT_PREFIX) + "Kills: " + Main.getInstance().getTotalKills(p));
            p.sendMessage(String.valueOf(Main.CHAT_PREFIX) + "Deaths: " + Main.getInstance().getDeaths(p));
            return true;
        }
        commandSender.sendMessage(String.valueOf(Main.CHAT_PREFIX) + ChatColor.RED + "Only players can execute this command!");
        return false;
    }
}
