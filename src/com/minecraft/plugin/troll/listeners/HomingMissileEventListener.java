package com.minecraft.plugin.troll.listeners;

import com.minecraft.plugin.troll.manager.Missile;
import com.minecraft.plugin.troll.manager.MissileManager;
import org.bukkit.*;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class HomingMissileEventListener implements Listener {

    @EventHandler
    public void onShoot(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        if (action == Action.RIGHT_CLICK_AIR && event.hasItem()) {
            if(event.getItem().getType() == Material.FIRE_CHARGE) {

                Player target = null;
                for (Player potential : Bukkit.getOnlinePlayers()) {
                    double potentialDistance = potential.getLocation().distance(player.getLocation());
                    if (potentialDistance > 20) {
                        if (target == null || potentialDistance < target.getLocation().distance(potential.getLocation())) {
                            target = potential;
                        }
                    }
                }

                if (target != null) {
                    if (player.getWorld().equals(target.getWorld())) {
                        Missile missile = new Missile(player, target);
                        MissileManager.add(missile);
                        missile.notifyLaunch();
                    }
                }
            }
        }
    }

    @EventHandler
    public void onHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        if (projectile instanceof Fireball) {
            Fireball fireball = (Fireball) projectile;
            if (MissileManager.contains(fireball)) {
                Missile missile = MissileManager.get(fireball);

                missile.getLocation().getWorld().createExplosion(missile.getLocation(), 100, true, true, missile.getEntity());
                int status;
                if(missile.getDistance() > 5)
                    status = 0;
                else
                    status = 1;

                missile.notifyStatus(status);
                missile.cleanUp();
            }
        }
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        for (Missile missile : MissileManager.getMissiles().values()) {
            if (player.getUniqueId().equals(missile.getLauncher().getUniqueId()) || player.getUniqueId().equals(missile.getTarget().getUniqueId())) {
                missile.cleanUp();
            }
        }
    }
}
