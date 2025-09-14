package de.redjulu.warPlugin;

import de.redjulu.warPlugin.gui.GUIManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public final class WarPlugin extends JavaPlugin {

    private static WarPlugin instance;

    public HashMap<UUID, Integer> money = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;

        Bukkit.getPluginManager().registerEvents(new GUIManager(), this);


    }

    @Override
    public void onDisable() {

    }

    public static WarPlugin getInstance() {
        return instance;
    }
}
