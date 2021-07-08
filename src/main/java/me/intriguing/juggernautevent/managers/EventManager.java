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

        timer = new CountdownTimer(plugin.getSettingsManager().countDownTime, plugin.getSettingsManager().notifyTimes, () -> {
            pickInitialRandomJuggernaut();
            beginGame();
        });
        timer.start();
        // Do not run below until game started.
    }

    // This method is here, and not just all
    // in this#pickRandomJuggernaut because EventListeners calls pickRandomJuggnaut
    // and it will not be null.
    public void pickInitialRandomJuggernaut() {
        if (juggernaut == null) {
            pickRandomJuggernaut();
        }
    }

    public void setJuggernaut(Player player) {
        this.juggernaut = player;

        Bukkit.getLogger().info("Juggernaut is now set to " + juggernaut.getName());
    }

    public void pickRandomJuggernaut() {
        this.setJuggernaut(Bukkit.getOnlinePlayers()
                .stream()
                .skip((int) (Bukkit.getOnlinePlayers().size() * Math.random()))
                .findFirst()
                .orElse(null));

        if (juggernaut == null) {
            throw new NullPointerException("New juggernaut player is null!");
        }
    }

    public void setJuggernautArmor() {
        this.juggernaut.getInventory().clear();
        Bukkit.getLogger().warning(InventoryManager.getJuggernautInventory().toString());
        InventoryManager.getJuggernautInventory().forEach((slot, item) -> {
                this.juggernaut.getInventory().setItem(slot, item);
        });
    }

    public void setAllNormalArmor() {
        plugin.getServer().getOnlinePlayers().forEach(this::setNormalArmor);
    }

    public void setNormalArmor(Player player) {
        if (player == juggernaut) {
            return;
        }

        player.getInventory().clear();
        InventoryManager.getNormalInventory().forEach((slot, item) ->
                player.getInventory().setItem(slot, item));
    }


    public void beginGame() {
        this.gameStarted = true;

        if (Bukkit.getOnlinePlayers().size() < 2) {
            plugin.getAdventure().players().sendMessage(MiniMessage.get().parse("There were only two players. Event canceling."));
            gameEnd();
            return;
        }

        plugin.getTasks().put("gameTimer", new BukkitRunnable() {
            @Override
            public void run() {
                plugin.getAdventure().players().sendMessage(MiniMessage.get().parse("<red>Congrats to " + juggernaut.getName() + " for winning the game!"));
                gameEnd();
            }
        }.runTaskLater(plugin, 20L * gameDuration.toSeconds()));

        this.setJuggernautArmor();
        this.setAllNormalArmor();

    }

    public void gameEnd() {
        this.gameStarted = false;
        this.running = false;
        this.juggernaut = null;
        this.gameDuration = null;

        plugin.getAdventure().players().sendMessage(MiniMessage.get().parse("Ending event..."));
        Bukkit.getOnlinePlayers().forEach(player -> {
            plugin.getEventListener().teleportPlayerToSpawn(player);
            player.getInventory().clear();
        });
        // Restart awaiting action bar event after game ends
        plugin.getTasks().put("actionbar", plugin.notRunningActionBar());
    }

}
