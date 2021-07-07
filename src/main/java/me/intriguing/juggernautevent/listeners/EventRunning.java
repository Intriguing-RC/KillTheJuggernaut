package me.intriguing.juggernautevent.listeners;

import me.intriguing.juggernautevent.Core;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
            plugin.getEventManager().pickNewJuggernaut();
        }
    }



}
