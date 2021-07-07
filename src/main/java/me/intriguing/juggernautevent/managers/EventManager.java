package me.intriguing.juggernautevent.managers;

import lombok.Getter;
import me.intriguing.juggernautevent.Core;
import me.intriguing.juggernautevent.util.CountdownTimer;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;
import java.time.Duration;

public class EventManager {

    @Getter private boolean gameStarted;
    @Getter private boolean running;
    private Duration gameDuration;
    @Getter private Player juggernaut;
    private CountdownTimer timer;
    private static Core plugin;

    public EventManager() {
        plugin = Core.getPlugin();
    }



    public void initialize(Duration duration, @Nullable Player juggernaut) {
        this.gameDuration = duration;
        this.juggernaut = juggernaut;

        this.running = true;
        Bukkit.getServer().getOnlinePlayers().forEach(player -> {
            player.teleport(plugin.getSettingsManager().arenaSpawnLocation);
            player.setGameMode(GameMode.SURVIVAL);
        });

        plugin.getTasks().get("actionbar").cancel();
        plugin.getTasks().remove("actionbar");

        timer = new CountdownTimer(plugin.getSettingsManager().countDownTime, plugin.getSettingsManager().notifyTimes);
        timer.start();
        // Do not run below until game started.
    }

    public void pickJuggernaut() {
        if (juggernaut == null) {
            pickNewJuggernaut();
        }
    }

    public void pickNewJuggernaut() {
        juggernaut = Bukkit.getOnlinePlayers()
                .stream()
                .skip((int) (Bukkit.getOnlinePlayers().size() * Math.random()))
                .findFirst()
                .orElse(null);

        if (juggernaut == null) {
            throw new NullPointerException("New juggernaut player is null!");
        }

        Bukkit.getLogger().info("Juggernaut is now set to " + juggernaut.getName());
    }

    public void beginGame() {
        this.gameStarted = true;

        plugin.getTasks().put("gameTimer", new BukkitRunnable() {
            @Override
            public void run() {
                plugin.getAdventure().players().sendMessage(MiniMessage.get().parse("<red>Congrats to " + juggernaut + " for winning the game!"));
                gameEnd();
            }
        }.runTaskLater(plugin, 20L * gameDuration.toSeconds()));



    }

    public void gameEnd() {
        this.gameStarted = false;
        this.running = false;

        plugin.getAdventure().players().sendMessage(MiniMessage.get().parse("Ending event..."));
        Bukkit.getOnlinePlayers().forEach(player -> plugin.getEventListener().teleportPlayerToSpawn(player));
        // Restart awaiting action bar event after game ends
        plugin.getTasks().put("actionbar", plugin.notRunningActionBar());
    }

}
