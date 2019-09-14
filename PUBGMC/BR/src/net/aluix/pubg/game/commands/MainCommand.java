package net.aluix.pubg.game.commands;

import java.io.IOException;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.aluix.pubg.Main;

public class MainCommand implements CommandExecutor
{
    public boolean onCommand(final CommandSender arg0, final Command arg1, final String arg2, final String[] args) {
        if (!(arg0 instanceof Player)) {
            return false;
        }
        final Player p = (Player)arg0;
        if (!p.isOp() || !p.hasPermission("pubg.setup")) {
            return false;
        }
        if (args.length < 1) {
            p.sendMessage(String.valueOf(Main.CHAT_PREFIX) + " Made by Godly");
        }
        else if (args[0].equalsIgnoreCase("setLobby")) {
            final Location loc = p.getLocation();
            if (loc != null) {
                Main.getInstance().cfg.set("Lobby.SpawnX", (Object)loc.getX());
                Main.getInstance().cfg.set("Lobby.SpawnY", (Object)loc.getY());
                Main.getInstance().cfg.set("Lobby.SpawnZ", (Object)loc.getZ());
                try {
                    p.sendMessage("You set the Lobby");
                    Main.getInstance().cfg.save(Main.getInstance().f);
                }
                catch (IOException e) {
                    p.sendMessage(String.valueOf(Main.CHAT_PREFIX) + " Couldn't reload config, check console for errors.");
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
}
