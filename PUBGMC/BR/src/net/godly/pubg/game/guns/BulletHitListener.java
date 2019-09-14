package net.godly.pubg.game.guns;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import net.godly.pubg.Main;
import net.godly.pubg.game.event.BulletHitEvent;
import net.godly.pubg.player.PUPlayer;
import net.godly.pubg.utils.PlayerUtils;

public class BulletHitListener implements Listener
{
    @EventHandler
    public void bulletHitEvent(final BulletHitEvent event) {
        System.out.println("bullet has hit!");
        final PUPlayer csShooter = Main.getInstance().getCSPlayer(event.getShooter());
        final PUPlayer csVictim = Main.getInstance().getCSPlayer(event.getVictim());
        double damageToDo = event.getGun().getDamage();
        if (event.getGun().isShotGun()) {
            damageToDo *= ((event.getShooter().getLocation().distance(event.getVictim().getLocation()) < 4.0) ? 3.0 : 1.5);
        }
        if (PlayerUtils.isHeadShot(event.getBullet(), csVictim.getPlayer()) && csVictim.getPlayer().getInventory().getHelmet() != null) {
            damageToDo *= 1.2;
            csVictim.getPlayer().getInventory().getHelmet().setDurability((short)0);
            csVictim.getPlayer().getInventory().getHelmet().setAmount(0);
        }
        else if (PlayerUtils.isHeadShot(event.getBullet(), csVictim.getPlayer()) && csVictim.getPlayer().getInventory().getHelmet() == null) {
            damageToDo *= 2.0;
        }
        event.getVictim().damage(damageToDo);
    }
}
