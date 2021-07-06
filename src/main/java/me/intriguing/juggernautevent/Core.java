package me.intriguing.juggernautevent;

import lombok.Getter;
import me.intriguing.juggernautevent.commands.EventCommand;
import me.intriguing.juggernautevent.commands.HelpCommand;
import me.intriguing.juggernautevent.commands.StartCommand;
import me.intriguing.juggernautevent.hooks.PlaceholderAPIHook;
import me.intriguing.juggernautevent.managers.EventManager;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.checkerframework.checker.nullness.qual.NonNull;

public class Core extends JavaPlugin {

    @Getter private static Core plugin;
    @Getter private EventManager eventManager;
    private BukkitAudiences adventure;

    public Core() {
        plugin = this;
    }

    @Override
    public void onEnable() {
        this.adventure = BukkitAudiences.create(this);

        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
            new PlaceholderAPIHook().register();
        }

        this.saveDefaultConfig();
        this.registerCommands();

        this.notRunningActionBar();
    }

    public @NonNull BukkitAudiences adventure() {
        if(this.adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return this.adventure;
    }

    private void registerCommands() {
        EventCommand baseCommand = new EventCommand();
        // Register Base Command
        this.getCommand("event").setExecutor(baseCommand);

        // Register Sub Commands
        baseCommand.registerSubCommand(new StartCommand());
        baseCommand.registerSubCommand(new HelpCommand());
    }

    @Override
    public void onDisable() {
        if(this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
    }

    public void notRunningActionBar() {
        BukkitTask scheduler = new BukkitRunnable() {
            @Override
            public void run() {
                adventure.players().sendActionBar(MiniMessage.get().parse("<player>"));
            }
        }.runTaskTimerAsynchronously(Core.getPlugin(), 0, 20 * 2L);
    }

}
