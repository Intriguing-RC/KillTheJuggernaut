package me.intriguing.juggernautevent.managers;

import me.intriguing.juggernautevent.Core;
import me.intriguing.juggernautevent.util.Config;
import me.intriguing.juggernautevent.util.ConfigurableLocation;
import org.bukkit.Location;

import java.io.File;
import java.util.*;

public class SettingsManager {

    private static final Core plugin = Core.getPlugin();
    public String helpMessage;
    public String reloadMessage;
    public String failedReloadMessage;
    public String noPermissionMessage;
    public String actionBarAwaiting;
    public Location waitingRoomLocation;
    public Location arenaSpawnLocation;
    public long countDownTime;
    public Set<Long> notifyTimes;
    public List<Map<?, ?>> juggernautInventory;
    public List<Map<?, ?>> normalInventory;
    public String reminderEventStarts;
    public String reminderEventEnds;
    public String juggernautRandom;
    public String juggernautNotRandom;
    public String notEnoughPlayers;
    public String announceWinner;
    public String endingEvent;
    public String canNotContinueNotEnoughPlayers;
    public String juggernautKilledChangePlayer;
    public String gameMustLastAtLeastOneMinute;
    public String invalidTimeProvided;
    public String successfulStart;
    public String invalidPlayer;
    public String eventAlreadyRunning;

    public void init() {
        plugin.getLogger().info("Loading configurations...");
        File file = new File(plugin.getDataFolder(), "config.yml");

        if(!file.exists()) {
            plugin.saveResource("config.yml", false);
        }

        Config config = Config.loadConfiguration(file);
        try {
            config.syncWithConfig(file, plugin.getResource("config.yml"), "items");
            plugin.getLogger().info("Loaded configurations.");
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to load configurations. Printing stack trace:");
            e.printStackTrace();
        }

        helpMessage = config.getString("messages.help");
        noPermissionMessage = config.getString("messages.no permission");
        reloadMessage = config.getString("messages.reload");
        failedReloadMessage = config.getString("messages.failed reload");
        actionBarAwaiting = config.getString("messages.action bar awaiting");
        waitingRoomLocation = ConfigurableLocation.build(Objects.requireNonNull(config.getConfigurationSection("waiting arena location")));
        arenaSpawnLocation = ConfigurableLocation.build(Objects.requireNonNull(config.getConfigurationSection("pvp arena.spawn location")));
        countDownTime = config.getLong("countdown.time");
        notifyTimes = new HashSet<>(config.getLongList("countdown.notify at"));
        juggernautInventory = config.getMapList("items.juggernaut");
        normalInventory = config.getMapList("items.normal");
        reminderEventStarts = config.getString("messages.reminder event start");
        reminderEventEnds = config.getString("messages.reminder event end");
        juggernautRandom = config.getString("messages.juggernaut random");
        juggernautKilledChangePlayer = config.getString("messages.juggernaut kill change player");
        juggernautNotRandom = config.getString("messages.juggernaut not random");
        notEnoughPlayers = config.getString("messages.not enough players");
        canNotContinueNotEnoughPlayers = config.getString("messages.cannot continue not enough players");
        announceWinner = config.getString("messages.announce winner");
        endingEvent = config.getString("messages.ending event");
        gameMustLastAtLeastOneMinute = config.getString("messages.game last least one minute");
        invalidTimeProvided = config.getString("messages.invalid time provided");
        successfulStart = config.getString("messages.successful start event");
        invalidPlayer = config.getString("messages.invalid player");
        eventAlreadyRunning = config.getString("messages.event already running");


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
