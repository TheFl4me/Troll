package com.minecraft.plugin.troll.manager;

import org.bukkit.entity.Fireball;

import java.util.HashMap;

public class MissileManager {

    private static HashMap<Fireball, Missile> missiles = new HashMap<>();

    public static void add(Missile missile) {
        missiles.put(missile.getEntity(), missile);
    }

    public static void remove(Fireball fireball) {
        missiles.remove(fireball);
    }

    public static boolean contains(Fireball fireball) {
        return missiles.containsKey(fireball);
    }

    public static Missile get(Fireball fireball) {
        return missiles.get(fireball);
    }

    public static HashMap<Fireball, Missile> getMissiles() {
        return missiles;
    }
}
