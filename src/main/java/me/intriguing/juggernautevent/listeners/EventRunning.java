package me.intriguing.juggernautevent.listeners;

import me.intriguing.juggernautevent.Core;
import me.intriguing.juggernautevent.managers.EventManager;
import me.intriguing.juggernautevent.managers.SettingsManager;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;

public class EventRunning implements Listener {

    private final Core plugin;
    private final EventManager event;
    private final SettingsManager config;
    private final BukkitAudiences adventure;

    public EventRunning() {
        plugin = Core.getPlugin();
        event = plugin.getEventManager();
        config = plugin.getSettingsManager();
        adventure = plugin.getAdventure();
    }

    @EventHandler
    public void handleEventStarted(PlayerJoinEvent e) {
        teleportPlayerToSpawn(e.getPlayer());
    }

    public void teleportPlayerToSpawn(Player player) {
        if (event.isRunning()) {
            Location joinLocation = config.arenaSpawnLocation;
            if (joinLocation != null) {
                player.teleport(joinLocation);
            } else {
                Bukkit.getLogger().severe("Join location is null!");
            }
        } else {
            Location joinLocation = config.waitingRoomLocation;
            if (joinLocation != null) {
                player.teleport(joinLocation);
            } else {
                Bukkit.getLogger().severe("Waiting location is null!");
            }
        }
    }

    @EventHandler
    public void awaitPlayerStart(PlayerMoveEvent e) {
        if (event.isRunning() && !event.isGameStarted()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void playerLeave(PlayerQuitEvent e) {
        if (!event.isGameStarted()) return;

        if (e.getPlayer() == event.getJuggernaut()) {
            event.pickRandomJuggernaut(e.getPlayer());
            event.setJuggernautArmor();
        }

        if (Bukkit.getOnlinePlayers().size() - 1 <= 1) {
            adventure.players().sendMessage(MiniMessage.get().parse(config.canNotContinueNotEnoughPlayers));
            event.getGameTimer().cancel();
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

                if (event.isGameStarted() && killer != null) {
                    if (killer != event.getJuggernaut()) {
                        plugin.getAdventure().players().sendMessage(
                                MiniMessage.get().parse(config.juggernautKilledChangePlayer, Template.of("killedplayer", player.getName()), Template.of("juggernaut", killer.getName())));
                        event.setJuggernaut(killer);
                        event.setJuggernautArmor();
                    }

                    event.setNormalArmor(player);
                }
            }
        }.runTaskLater(plugin, 1L);
    }

    @EventHandler
    public void playerDropItemEvent(PlayerDropItemEvent e) {
        if (event.isGameStarted()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void playerRespawn(PlayerRespawnEvent e) {
        // Using isRunning instead of isGameStarted just in case player dies during waiting game to start (in waiting area)
        if (event.isRunning()) {
            e.setRespawnLocation(config.arenaSpawnLocation);
        } else {
            e.setRespawnLocation(config.waitingRoomLocation);
        }
    }



}
