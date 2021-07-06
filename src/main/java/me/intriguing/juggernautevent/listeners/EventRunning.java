package me.intriguing.juggernautevent.listeners;

import me.intriguing.juggernautevent.Core;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class EventRunning implements Listener {

    private static Core plugin;

    public EventRunning() {
        plugin = Core.getPlugin();
    }

    @EventHandler
    public void handleEventStarted(PlayerJoinEvent e) {
        if (plugin.getEventManager().isRunning()) {
            // TODO: If event is running (checked above), then teleport player
            //  to event arena with spectator mode.
        } else {
            // TODO: Teleport player to waiting area
            Location joinLocation = plugin.getSettingsManager().waitingRoomLocation;
            if (joinLocation != null) {
                e.getPlayer().teleport(joinLocation);
            }
        }
    }



}
