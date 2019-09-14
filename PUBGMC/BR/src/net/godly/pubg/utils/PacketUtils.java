package net.godly.pubg.utils;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import net.godly.pubg.Main;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;

public class PacketUtils
{
    public static void sendActionBar(final Player player, final String text, final int duration) {
        final IChatBaseComponent cbc = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + text + "\"}");
        final PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc, (byte)2);
        new BukkitRunnable() {
            private int count = 0;
            
            public void run() {
                if (this.count >= duration - 3) {
                    this.cancel();
                }
                ((CraftPlayer)player).getHandle().playerConnection.sendPacket((Packet)ppoc);
                ++this.count;
            }
        }.runTaskTimer((Plugin)Main.getInstance(), 0L, 20L);
    }
}
