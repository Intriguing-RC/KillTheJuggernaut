package com.theplonk.juggernautevent;

import com.theplonk.juggernautevent.commands.*;
import com.theplonk.juggernautevent.hooks.PlaceholderAPIHook;
import com.theplonk.juggernautevent.listeners.EventRunning;
import com.theplonk.juggernautevent.managers.EventManager;
import com.theplonk.juggernautevent.managers.SettingsManager;
import lombok.Getter;
import me.intriguing.juggernautevent.commands.*;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Core extends JavaPlugin {

    @Getter private static Core plugin;
    @Getter private EventManager eventManager;
    @Getter private SettingsManager settingsManager;
    @Getter private EventRunning eventListener;
    @Getter private final Map<String, BukkitTask> tasks = new HashMap<>();
    private BukkitAudiences adventure;

    public Core() {
        plugin = this;
    }

    @Override
    public void onEnable() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderAPIHook().register();
        }

        this.adventure = BukkitAudiences.create(this);
        this.settingsManager = new SettingsManager();
        this.eventManager = new EventManager();
        settingsManager.init();

        this.registerEvents();
        this.registerCommands();
        tasks.put("actionbar", this.notRunningActionBar());

    }

    public @NonNull BukkitAudiences getAdventure() {
        if(this.adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return this.adventure;
    }

    private void registerCommands() {
        EventCommand baseCommand = new EventCommand();
        // Register Base Command
        Objects.requireNonNull(this.getCommand("event")).setExecutor(baseCommand);

        // Register Sub Commands
        baseCommand.registerSubCommand(new StartCommand());
        baseCommand.registerSubCommand(new HelpCommand());
        baseCommand.registerSubCommand(new ReloadCommand());
        baseCommand.registerSubCommand(new CancelCommand());
    }

    private void registerEvents() {
        eventListener = new EventRunning();
        this.getServer().getPluginManager().registerEvents(eventListener, this);
    }

    @Override
    public void onDisable() {
        if(this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
    }

    public BukkitTask notRunningActionBar() {
        return new BukkitRunnable() {
            @Override
            public void run() {
                adventure.players().sendActionBar(MiniMessage.get().parse(settingsManager.actionBarAwaiting));
            }
        }.runTaskTimerAsynchronously(Core.getPlugin(), 0, 20 * 2L);
    }

}
