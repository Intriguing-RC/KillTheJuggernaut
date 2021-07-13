package com.theplonk.juggernautevent.listeners;

import com.theplonk.juggernautevent.Core;
import com.theplonk.juggernautevent.managers.EventManager;
import com.theplonk.juggernautevent.managers.SettingsManager;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
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
        e.getPlayer().getActivePotionEffects().forEach(potionEffect -> e.getPlayer().removePotionEffect(potionEffect.getType()));
        e.getPlayer().setHealth(20.0f);
        e.getPlayer().setSaturation(20.0f);
        e.getPlayer().setExp(0.0f);
        e.getPlayer().setLevel(0);
        e.getPlayer().setGlowing(false);
        e.getPlayer().getInventory().clear();
        teleportPlayerToSpawn(e.getPlayer());
    }

    public void teleportPlayerToSpawn(Player player) {
        if (event.isRunning()) {
            Location joinLocation = config.arenaSpawnLocation;
            if (joinLocation != null) {
                if (!player.hasPermission("event.exempt")) {
                    event.getPlayingList().add(player);
                    if (event.isGameStarted()) {
                        event.setNormalArmor(player);
                    }
                    player.teleport(joinLocation);
                }
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
            if (event.getPlayingList().contains(e.getPlayer())) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void playerLeave(PlayerQuitEvent e) {
        if (!event.isGameStarted()) return;

        e.getPlayer().getInventory().clear();

        event.getPlayingList().remove(e.getPlayer());

        if (e.getPlayer() == event.getJuggernaut()) {
            event.pickRandomJuggernaut(e.getPlayer());
            event.getJuggernaut().setGlowing(true);
            event.setJuggernautArmor();
        }

        if (event.getPlayingList().size() <= 1) {
            adventure.players().sendMessage(MiniMessage.get().parse(config.canNotContinueNotEnoughPlayers));
            event.getGameTimer().cancelAndRun();
        }
    }

    @EventHandler
    public void deleteArrow(ProjectileHitEvent e) {
        e.getEntity().remove();
    }

    @EventHandler
    public void disablePvPWhileWaiting(EntityDamageByEntityEvent e) {
        if (event.isRunning() && !event.isGameStarted()) {
            if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
                e.setCancelled(true);
            }
        }

        if (event.isRunning()) {
            if (e.getDamager() instanceof Player) {
                if (!event.getPlayingList().contains((Player) e.getDamager())) {
                    e.setCancelled(true);
                    adventure.sender(e.getDamager()).sendMessage(MiniMessage.get().parse(config.cantHitPlayers));
                }
            }
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


                if (event.isGameStarted()) {
                    if (killer == null) {
                        if (player != event.getJuggernaut()) {
                            event.setNormalArmor(player);
                        } else {
                            event.setJuggernautArmor();
                        }
                    }

                    else if (killer != event.getJuggernaut() && player == event.getJuggernaut()) {
                        plugin.getAdventure().players().sendMessage(
                                MiniMessage.get().parse(config.juggernautKilledChangePlayer, Template.of("killedplayer", player.getName()), Template.of("juggernaut", killer.getName())));
                        event.setJuggernaut(killer);
                        event.getJuggernaut().setGlowing(true);
                        event.setJuggernautArmor();

                        player.setGlowing(false);
                        event.setNormalArmor(player);
                    } else if (killer == event.getJuggernaut() && player == event.getJuggernaut()) {
                        event.setJuggernautArmor();
                    }
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
