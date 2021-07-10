package me.intriguing.juggernautevent.listeners;

import me.intriguing.juggernautevent.Core;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;

public class EventRunning implements Listener {

    private final Core plugin;
    private final BukkitAudiences adventure;

    public EventRunning() {
        plugin = Core.getPlugin();
        adventure = plugin.getAdventure();
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
            plugin.getEventManager().pickRandomJuggernaut(e.getPlayer());
            plugin.getEventManager().setJuggernautArmor();
        }

        if (Bukkit.getOnlinePlayers().size() - 1 <= 1) {
            adventure.players().sendMessage(MiniMessage.get().parse("<red>There are now less than two players on the event!"));
            plugin.getEventManager().getGameTimer().cancel();
        }
    }



    @EventHandler
    public void playerDeathHandler(PlayerDeathEvent e) {

        e.getDrops().clear();

        Player killer = e.getEntity().getKiller();
        Player player = e.getEntity();

        new BukkitRunnable() {
            @Override
            public void run() {
                player.spigot().respawn();

                if (plugin.getEventManager().isGameStarted() && killer != null) {
                    if (killer != plugin.getEventManager().getJuggernaut()) {
                        plugin.getAdventure().players().sendMessage(MiniMessage.get().parse("<red><name> is now the juggernaut!", Template.of("name", killer.getName())));
                        plugin.getEventManager().setJuggernaut(killer);
                        plugin.getEventManager().setJuggernautArmor();
                    }

                    plugin.getEventManager().setNormalArmor(player);
                }
            }
        }.runTaskLater(plugin, 1L);
    }

    @EventHandler
    public void playerDropItemEvent(PlayerDropItemEvent e) {
        if (plugin.getEventManager().isGameStarted()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void playerRespawn(PlayerRespawnEvent e) {
        // Using isRunning instead of isGameStarted just in case player dies during waiting game to start (in waiting area)
        if (plugin.getEventManager().isRunning()) {
            e.setRespawnLocation(plugin.getSettingsManager().arenaSpawnLocation);
        } else {
            e.setRespawnLocation(plugin.getSettingsManager().waitingRoomLocation);
        }
    }



}
