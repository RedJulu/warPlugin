package de.redjulu.warPlugin;

import de.redjulu.warPlugin.commands.VanishCommand;
import de.redjulu.warPlugin.gui.GUIManager;
import de.redjulu.warPlugin.listeners.MainListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public final class WarPlugin extends JavaPlugin {

    private static WarPlugin instance;

    public HashMap<UUID, Integer> ranks = new HashMap<>();
    public HashMap<UUID, Integer> money = new HashMap<>();
    public HashMap<UUID, Boolean> vanish = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;

        //DEBUG!!
        ranks.put(UUID.fromString("e653bee0-27f0-4c2e-98a2-3f8fd4b55ef6"), 2);

        Bukkit.getPluginManager().registerEvents(new GUIManager(), this);
        Bukkit.getPluginManager().registerEvents(new MainListener(), this);
        getCommand("vanish").setExecutor(new VanishCommand());


    }

    @Override
    public void onDisable() {

    }

    public static WarPlugin getInstance() {
        return instance;
    }
}
