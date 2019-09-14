package net.aluix.pubg.game.guns;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import net.aluix.pubg.game.event.WeaponFireEvent;

public class WeaponFireListener implements Listener
{
    @EventHandler
    public void weaponFireEvent(final WeaponFireEvent event) {
        event.getPlayer().setLevel(event.getGun().getAmmunition());
        if (event.getGun().getAmmunition() <= 0) {
            event.getGun().reload(event.getPlayer());
        }
    }
}
