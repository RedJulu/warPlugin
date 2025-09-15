package de.redjulu.warPlugin;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.redjulu.warPlugin.commands.MoneyCommand;
import de.redjulu.warPlugin.commands.RankCommand;
import de.redjulu.warPlugin.commands.VanishCommand;
import de.redjulu.warPlugin.gui.GUIManager;
import de.redjulu.warPlugin.listeners.MainListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class WarPlugin extends JavaPlugin {

    private static WarPlugin instance;
    private final Gson gson = new Gson();

    public HashMap<UUID, Integer> ranks = new HashMap<>();
    public HashMap<UUID, Integer> money = new HashMap<>();
    public HashMap<UUID, Boolean> vanish = new HashMap<>();

    public File ranksFile;
    public File moneyFile;
    public File vanishFile;

    @Override
    public void onEnable() {
        instance = this;


        // Dateien anlegen
        File dataFolder = getDataFolder();
        dataFolder.mkdirs();
        ranksFile = new File(dataFolder, "ranks.json");
        moneyFile = new File(dataFolder, "money.json");
        vanishFile = new File(dataFolder, "vanish.json");

        if (!ranksFile.exists()) saveMap(ranksFile, ranks);
        if (!moneyFile.exists()) saveMap(moneyFile, money);
        if (!vanishFile.exists()) saveMap(vanishFile, vanish);

        loadMap(ranksFile, ranks, Integer.class);
        loadMap(moneyFile, money, Integer.class);
        loadMap(vanishFile, vanish, Boolean.class);

        Bukkit.getPluginManager().registerEvents(new GUIManager(), this);
        Bukkit.getPluginManager().registerEvents(new MainListener(), this);
        getCommand("vanish").setExecutor(new VanishCommand());
        getCommand("rank").setExecutor(new RankCommand());
        getCommand("money").setExecutor(new MoneyCommand());
        getCommand("money").setTabCompleter(new MoneyCommand());


        getLogger().info("WarPlugin aktiviert!");
    }

    @Override
    public void onDisable() {
        saveMap(ranksFile, ranks);
        saveMap(moneyFile, money);
        saveMap(vanishFile, vanish);

        getLogger().info("WarPlugin deaktiviert – alle Daten gespeichert!");
    }

    public static WarPlugin getInstance() {
        return instance;
    }

    // Generische Map speichern
    public <T> void saveMap(File file, Map<UUID, T> map) {
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(map, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Generische Map laden mit korrektem Typ-Cast
    public <T> void loadMap(File file, Map<UUID, T> map, Class<T> clazz) {
        try (FileReader reader = new FileReader(file)) {
            Type type = new TypeToken<Map<UUID, Object>>() {}.getType();
            Map<UUID, Object> loaded = gson.fromJson(reader, type);
            if (loaded != null) {
                map.clear();
                loaded.forEach((uuid, value) -> {
                    if (clazz == Integer.class) map.put(uuid, clazz.cast(((Number) value).intValue()));
                    else if (clazz == Boolean.class) map.put(uuid, clazz.cast(value));
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
