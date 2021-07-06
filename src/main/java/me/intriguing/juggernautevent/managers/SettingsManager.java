package me.intriguing.juggernautevent.managers;

import me.intriguing.juggernautevent.Core;
import me.intriguing.juggernautevent.util.Config;
import me.intriguing.juggernautevent.util.ConfigurableLocation;
import org.bukkit.Location;

import java.io.File;
import java.util.List;
import java.util.Objects;

public class SettingsManager {

    private static final Core plugin = Core.getPlugin();
    private static Config config;
    public List<String> helpMessage;
    public String reloadMessage;
    public String failedReloadMessage;
    public String noPermissionMessage;
    public String actionBarAwaiting;
    public Location waitingRoomLocation;

    public void init() {
        plugin.getLogger().info("Loading configurations...");
        File file = new File(plugin.getDataFolder(), "config.yml");

        if(!file.exists()) {
            plugin.saveResource("config.yml", false);
        }

        config = Config.loadConfiguration(file);
        try {
            config.syncWithConfig(file, plugin.getResource("config.yml"));
            plugin.getLogger().info("Loaded configurations...");
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to load configurations. Printing stack trace:");
            e.printStackTrace();
        }

        helpMessage = config.getStringList("messages.help");
        noPermissionMessage = config.getString("messages.no-permission");
        reloadMessage = config.getString("messages.reload");
        failedReloadMessage = config.getString("messages.failed-reload");
        actionBarAwaiting = config.getString("messages.action-bar-awaiting");
        waitingRoomLocation = new ConfigurableLocation(Objects.requireNonNull(config.getConfigurationSection("waiting arena location"))).getLocation();
    }

    public boolean reloadConfiguration() {
        try {
            init();
        } catch(Exception ex){
            ex.printStackTrace();
            return false;
        }

        return true;
    }
}
