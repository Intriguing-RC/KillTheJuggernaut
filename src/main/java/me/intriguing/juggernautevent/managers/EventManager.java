package me.intriguing.juggernautevent.managers;

import lombok.Getter;
import lombok.Setter;
import me.intriguing.juggernautevent.Core;
import me.intriguing.juggernautevent.util.CountdownTimer;
import me.intriguing.juggernautevent.util.TimerUtil;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.joda.time.Duration;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class EventManager {

    @Getter private boolean gameStarted;
    private SettingsManager config;
    @Getter private boolean running;
    private Duration gameDuration;
    @Setter @Getter private Player juggernaut;
    @Getter private List<Player> playingList;
    @Getter private CountdownTimer timer;
    @Getter private CountdownTimer gameTimer;
    private final Core plugin;

    public EventManager() {
        plugin = Core.getPlugin();
        config = plugin.getSettingsManager();
    }

    public void initialize(Duration duration, @Nullable Player juggernaut, List<Player> playingList) {
        this.gameDuration = duration;
        this.juggernaut = juggernaut;
        this.playingList = playingList;

        this.running = true;
        playingList.forEach(player -> {
            player.teleport(config.arenaSpawnLocation);
            player.setGameMode(GameMode.SURVIVAL);
        });

        plugin.getTasks().get("actionbar").cancel();
        plugin.getTasks().remove("actionbar");

        timer = new CountdownTimer(config.countDownTime, config.notifyTimes, () -> {
            if (juggernaut == null) pickRandomJuggernaut(null);
            else {
                plugin.getAdventure().players().sendMessage(MiniMessage.get().parse(
                        config.juggernautNotRandom, Template.of("name", juggernaut.getName())));
            }
            beginGame();
        }, config.reminderEventStarts, null);
        timer.start();
        // Nothing runs below
    }

    public void pickRandomJuggernaut(Player exclude) {
        List<Player> playersExclude = new ArrayList<>(playingList);
        if (exclude != null) {
            playersExclude.remove(exclude);
        }

        this.setJuggernaut(playersExclude
                .stream()
                .skip((int) (playersExclude.size() * Math.random()))
                .findFirst()
                .orElse(null));

        plugin.getAdventure().players().sendMessage(MiniMessage.get().parse(
                config.juggernautRandom, Template.of("name", juggernaut.getName())));

        if (juggernaut == null) {
            Bukkit.getLogger().severe("Juggernaut player is null!");
        }
    }

    public void setJuggernautArmor() {
        this.juggernaut.getInventory().clear();
        InventoryManager.getJuggernautInventory().forEach((slot, item) -> {
                this.juggernaut.getInventory().setItem(slot, item);
        });
    }

    public void setAllNormalArmor() {
        playingList.forEach(this::setNormalArmor);
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

        if (playingList.size() < 2) {
            plugin.getAdventure().players().sendMessage(MiniMessage.get().parse(config.notEnoughPlayers));
            gameEnd();
            return;
        }

        playingList.forEach(player -> {
            player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
            player.setHealth(20.0D);
            player.setSaturation(20.0f);
        });

        gameTimer = new CountdownTimer(gameDuration.getStandardSeconds(), null, () -> {
            plugin.getAdventure().players().sendMessage(MiniMessage.get().parse(config.announceWinner, Template.of("name", this.getJuggernaut().getName())));
            gameEnd();
        }, config.reminderEventEnds, TimerUtil::automaticRemind);
        gameTimer.start();

        this.juggernaut.setGlowing(true);
        this.setJuggernautArmor();
        this.setAllNormalArmor();

    }

    public void gameEnd() {
        this.gameStarted = false;
        this.running = false;
        this.juggernaut = null;
        this.gameDuration = null;

        plugin.getAdventure().players().sendMessage(MiniMessage.get().parse(config.endingEvent));
        playingList.forEach(player -> {
            plugin.getEventListener().teleportPlayerToSpawn(player);
            player.setGlowing(false);
            player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
            player.setHealth(20.0D);
            player.setSaturation(20.0f);
            player.getInventory().clear();
        });
        // Restart awaiting action bar event after game ends
        plugin.getTasks().put("actionbar", plugin.notRunningActionBar());
    }

}
