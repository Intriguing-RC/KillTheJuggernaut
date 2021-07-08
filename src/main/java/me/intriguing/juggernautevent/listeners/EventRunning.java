package me.intriguing.juggernautevent.listeners;

import me.intriguing.juggernautevent.Core;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventRunning implements Listener {

    private static Core plugin;

    public EventRunning() {
        plugin = Core.getPlugin();
    }

    @EventHandler
    public void handleEventStarted(PlayerJoinEvent e) {
        teleportPlayerToSpawn(e.getPlayer());
    }

    public void teleportPlayerToSpawn(Player player) {
        if (plugin.getEventManager().isRunning()) {
            Location joinLocation = plugin.getSettingsManager().arenaSpawnLocation;
            if (joinLocation != null) {
                player.teleport(joinLocation);
            }

            if (plugin.getEventManager().isGameStarted()) {
                player.setGameMode(GameMode.SPECTATOR);
            } else {
                player.setGameMode(GameMode.SURVIVAL);
            }
        } else {
            Location joinLocation = plugin.getSettingsManager().waitingRoomLocation;
            if (joinLocation != null) {
                player.teleport(joinLocation);
            }
        }
    }

    @EventHandler
    public void awaitPlayerStart(PlayerMoveEvent e) {
        if (plugin.getEventManager().isRunning() && !plugin.getEventManager().isGameStarted()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void playerLeave(PlayerQuitEvent e) {
        if (!plugin.getEventManager().isGameStarted()) return;

        if (e.getPlayer() == plugin.getEventManager().getJuggernaut()) {
            plugin.getEventManager().pickRandomJuggernaut();
        }

        if (Bukkit.getOnlinePlayers().size() <= 1) {
            plugin.getAdventure().players().sendMessage(MiniMessage.get().parse("<red>Congrats to " +
                    plugin.getEventManager().getJuggernaut().getName() + " for winning the game!"));
            plugin.getEventManager().gameEnd();
        }
    }



    @EventHandler
    public void playerDeathHandler(PlayerDeathEvent e) {

        e.getEntity().spigot().respawn();

        Player killer = e.getEntity().getKiller();
        Player player = e.getEntity();

        if (plugin.getEventManager().isGameStarted() && killer != null && killer == plugin.getEventManager().getJuggernaut()) {
            plugin.getEventManager().setJuggernaut(killer);
            plugin.getEventManager().setJuggernautArmor();

            plugin.getEventManager().setNormalArmor(player);
        }
    }



}
