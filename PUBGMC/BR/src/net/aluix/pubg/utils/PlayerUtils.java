package net.aluix.pubg.utils;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.util.Vector;

public class PlayerUtils
{
    public static void shakeScreen(final Player player, final int randomBound) {
        final Random r = new Random();
        final Location shakeLocation = player.getLocation().clone();
        shakeLocation.setPitch(shakeLocation.getPitch() - r.nextFloat() * randomBound);
        shakeLocation.setYaw(shakeLocation.getYaw() - r.nextFloat() * randomBound);
        player.teleport(shakeLocation);
    }
    
    public static Location getRightHeadLocation(final LivingEntity entity) {
        final Location eyeLocation = entity.getEyeLocation();
        final Location newLocation = eyeLocation.add(rotateRight90Degrees(eyeLocation.getDirection()).multiply(0.2));
        return newLocation;
    }
    
    public static Vector rotateRight90Degrees(final Vector vector) {
        final double x = vector.getX();
        vector.setX(-vector.getZ()).setZ(x);
        return vector;
    }
    
    public static boolean isHeadShot(final Projectile bullet, final Player player) {
        final double projectile_height = bullet.getLocation().getY();
        final double player_bodyheight = player.getLocation().getY() + 1.6;
        System.out.println("Body height = " + player_bodyheight);
        System.out.println("Projectile height = " + projectile_height);
        if (projectile_height > player_bodyheight) {
            System.out.println();
            return true;
        }
        return false;
    }
    
    public static boolean playerBehindPlayer(final Player playerBehind, final Player playerNotBehind) {
        return Math.abs(playerBehind.getLocation().getDirection().angle(playerNotBehind.getLocation().getDirection())) <= 45.0f;
    }
}
