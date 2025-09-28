package de.redjulu.warPlugin;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.redjulu.warPlugin.commands.BankCommand;
import de.redjulu.warPlugin.commands.team.InvseeCommand;
import de.redjulu.warPlugin.commands.team.MoneyCommand;
import de.redjulu.warPlugin.commands.team.RankCommand;
import de.redjulu.warPlugin.commands.team.VanishCommand;
import de.redjulu.warPlugin.gui.GUIManager;
import de.redjulu.warPlugin.listeners.ChatListener;
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

    // Player Data
    public HashMap<UUID, Integer> ranks = new HashMap<>();
    public HashMap<UUID, String> org = new HashMap<>();
    public HashMap<UUID, Integer> money = new HashMap<>();
    public HashMap<UUID, Boolean> vanish = new HashMap<>();
    public HashMap<UUID, Boolean> modify = new HashMap<>();

    // Vanish Options
    public HashMap<UUID, Boolean> vanishPickup = new HashMap<>();
    public HashMap<UUID, Boolean> vanishChest = new HashMap<>();

    // Files
    public File ranksFile;
    public File orgFile;
    public File moneyFile;
    public File vanishFile;
    public File vanishPickupFile;
    public File vanishChestFile;

    @Override
    public void onEnable() {
        instance = this;
        InvseeCommand invsee = new InvseeCommand();

        ChatListener chatListener = new ChatListener();
        Bukkit.getPluginManager().registerEvents(chatListener, this);

        // Dateien anlegen
        File dataFolder = getDataFolder();
        dataFolder.mkdirs();
        ranksFile = new File(dataFolder, "ranks.json");
        orgFile = new File(dataFolder, "org.json");
        moneyFile = new File(dataFolder, "money.json");
        vanishFile = new File(dataFolder, "vanish.json");
        vanishPickupFile = new File(dataFolder, "vanishPickup.json");
        vanishChestFile = new File(dataFolder, "vanishChest.json");

        if (!ranksFile.exists()) saveMap(ranksFile, ranks);
        if (!orgFile.exists()) saveMap(orgFile, org);
        if (!moneyFile.exists()) saveMap(moneyFile, money);
        if (!vanishFile.exists()) saveMap(vanishFile, vanish);
        if (!vanishPickupFile.exists()) saveMap(vanishPickupFile, vanishPickup);
        if (!vanishChestFile.exists()) saveMap(vanishChestFile, vanishChest);

        loadMap(ranksFile, ranks, Integer.class);
        loadMap(orgFile, org, String.class);
        loadMap(moneyFile, money, Integer.class);
        loadMap(vanishFile, vanish, Boolean.class);
        loadMap(vanishPickupFile, vanishPickup, Boolean.class);
        loadMap(vanishChestFile, vanishChest, Boolean.class);

        // Listener
        Bukkit.getPluginManager().registerEvents(new GUIManager(), this);
        Bukkit.getPluginManager().registerEvents(new MainListener(), this);
        Bukkit.getPluginManager().registerEvents(new ChatListener(), this);
        Bukkit.getPluginManager().registerEvents(invsee, this);

        // Commands
        getCommand("vanish").setExecutor(new VanishCommand(instance));
        getCommand("rank").setExecutor(new RankCommand());
        getCommand("money").setExecutor(new MoneyCommand());
        getCommand("bank").setExecutor(new BankCommand());
        getCommand("invsee").setExecutor(invsee);

        getCommand("money").setTabCompleter(new MoneyCommand());

        ScoreboardClass.startUpdater();

        getLogger().info("WarPlugin aktiviert!");
    }

    @Override
    public void onDisable() {
        saveMap(ranksFile, ranks);
        saveMap(orgFile, org);
        saveMap(moneyFile, money);
        saveMap(vanishFile, vanish);
        saveMap(vanishPickupFile, vanishPickup);
        saveMap(vanishChestFile, vanishChest);

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
