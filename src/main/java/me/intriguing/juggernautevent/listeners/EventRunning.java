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
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.PlayerInventory;
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
            plugin.getEventManager().pickRandomJuggernaut();
        }

        if (Bukkit.getOnlinePlayers().size() <= 1) {
            adventure.players().sendMessage(MiniMessage.get().parse("<red>Congrats to " +
                    plugin.getEventManager().getJuggernaut().getName() + " for winning the game!"));
            plugin.getEventManager().gameEnd();
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
                    plugin.getAdventure().players().sendMessage(MiniMessage.get().parse("<red><name> is now the juggernaut!", Template.of("name", killer.getName())));
                    plugin.getEventManager().setJuggernaut(killer);
                    plugin.getEventManager().setJuggernautArmor();

                    plugin.getEventManager().setNormalArmor(player);
                }
            }
        }.runTaskLater(plugin, 1L);
    }

    @EventHandler
    public void playerDropItemEvent(PlayerDropItemEvent e) {
        System.out.println("Event Called");

        if (plugin.getEventManager().isGameStarted()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void playerInventoryInteract(InventoryInteractEvent e) {
        if (plugin.getEventManager().isGameStarted()) {
            if (e.getWhoClicked() instanceof Player && e.getInventory() instanceof PlayerInventory) {
                e.setCancelled(true);
            }
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
