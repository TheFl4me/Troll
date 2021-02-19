package com.minecraft.plugin.troll;

import com.minecraft.plugin.troll.listeners.HomingMissileEventListener;
import org.bukkit.plugin.java.JavaPlugin;

public class Troll extends JavaPlugin {

    private static Troll plugin;

    public void onEnable() {
        plugin = this;

        getServer().getPluginManager().registerEvents(new HomingMissileEventListener(), this);
    }

    public static Troll getPlugin() {
        return plugin;
    }
}
