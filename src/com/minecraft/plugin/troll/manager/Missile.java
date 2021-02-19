package com.minecraft.plugin.troll.manager;

import com.minecraft.plugin.troll.Troll;
import org.bukkit.*;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;

public class Missile {

    private Fireball fireball;
    private Player launcher;
    private Player target;
    private BukkitRunnable homingTask;

    public Missile(Player launcher, Player target) {
        this.fireball = launcher.launchProjectile(Fireball.class);
        this.launcher = launcher;
        this.target = target;
        this.homingTask = new BukkitRunnable() {
            @Override
            public void run() {
                adjustCourse();
                notifyStatus(2);
            }
        };
        this.getHomingTask().runTaskTimer(Troll.getPlugin(), 40, 1);
    }

    public Fireball getEntity() {
        return this.fireball;
    }

    public Player getLauncher() {
        return this.launcher;
    }

    public Player getTarget() {
        return this.target;
    }

    public BukkitRunnable getHomingTask() {
        return this.homingTask;
    }

    public double getDistance() {
        return this.getLocation().distance(this.getTarget().getLocation());
    }

    public Location getLocation() {
        return this.getEntity().getLocation();
    }

    public void adjustCourse() {

        final double ACCURACY = 0.15;
        final double ACCURACY_INCREASE_DISTANCE_THRESHOLD = 4;
        final double SPEED = 1;

        double addition;
        if (this.getDistance() > ACCURACY_INCREASE_DISTANCE_THRESHOLD)
            addition = 0;
        else
            addition = (ACCURACY_INCREASE_DISTANCE_THRESHOLD + 1) - this.getDistance() / 10;
        double localAccuracy = ACCURACY + addition;


        this.getEntity().setVelocity(this.getEntity().getVelocity().add(this.getTarget().getLocation().subtract(this.getLocation()).toVector().normalize().multiply(localAccuracy * SPEED)).normalize().multiply(SPEED));

        this.getLocation().getWorld().spawnParticle(Particle.SMOKE_LARGE, this.getLocation(), 1);
    }

    public void notifyLaunch() {
        this.getTarget().playSound(this.getTarget().getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER,1, 1);
        this.getLauncher().playSound(this.getLauncher().getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER,1, 1);

        this.getTarget().sendTitle(ChatColor.DARK_RED + "ALERT!", ChatColor.RED + "MISSILE LAUNCH DETECTED!", 0, 40, 0);
    }

    public void notifyStatus(int i) {
        DecimalFormat df = new DecimalFormat("#.0");

        String status;
        switch (i) {
            case 0:
                status = ChatColor.RED + "FAILED";
                break;
            case 1:
                status = ChatColor.GREEN + "SUCCESS";
                break;
            default:
                status = ChatColor.YELLOW + "PENDING";
        }

        this.getLauncher().sendTitle(ChatColor.GOLD + "MISSILE STATUS: " + status, ChatColor.GOLD + "Distance: " + df.format(this.getDistance()), 0, 40, 0 );

        this.getTarget().sendTitle(ChatColor.RED + "INCOMING MISSILE!", ChatColor.GOLD + "Distance: " + df.format(this.getDistance()), 0, 3, 0);
        this.getTarget().playSound(this.getTarget().getLocation(), Sound.BLOCK_ANVIL_PLACE,1, 1);
    }

    public void cleanUp() {
        this.getHomingTask().cancel();
        MissileManager.remove(this.getEntity());
    }
}
