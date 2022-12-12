package com.minecraft.plugin.troll.listeners;

import com.minecraft.plugin.troll.Troll;
import com.minecraft.plugin.troll.manager.Missile;
import com.minecraft.plugin.troll.manager.MissileManager;
import org.bukkit.*;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.Random;

public class HomingMissileEventListener implements Listener {

    @EventHandler
    public void onShoot(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        if (action == Action.RIGHT_CLICK_AIR && event.hasItem()) {
            if(event.getItem().getType() == Material.FIRE_CHARGE) {

                Player target = null;
                for (Player potential : Bukkit.getOnlinePlayers()) {
                    if (potential.getLocation().getWorld().equals(player.getLocation().getWorld())) {
                        double potentialDistance = potential.getLocation().distance(player.getLocation());
                        if (potentialDistance > 20) {
                            if (target == null || potentialDistance < target.getLocation().distance(potential.getLocation())) {
                                target = potential;
                            }
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

    @EventHandler
    public void onDestroyDiamond(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (event.getBlock().getType() == Material.DIAMOND_ORE) { //UUID of Bene 8caafe0d-b501-42e8-85ed-70d34f6a177c
            Random r = new Random();
            int per = r.nextInt(10);
            if (per <= 2) {
                player.playSound(player.getLocation(), Sound.ENTITY_WARDEN_EMERGE, 1, 1);
                BukkitTask taskID = Bukkit.getScheduler().runTaskTimer(Troll.getPlugin(), () -> {
                    Random r2 = new Random();
                    Sound sound;
                    int per1 = r2.nextInt(10);
                    if (per1 <= 2) {
                        player.playSound(player.getLocation(), Sound.ENTITY_WARDEN_SNIFF, 1, 1);
                    }
                    player.playSound(player.getLocation(), Sound.ENTITY_WARDEN_HEARTBEAT, 1, 1);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 12 * 20, 0));
                },20 * 8, 20 * 2);


                Bukkit.getScheduler().runTaskLater(Troll.getPlugin(), taskID::cancel, 20 * 40);
            }
        }
    }
}
