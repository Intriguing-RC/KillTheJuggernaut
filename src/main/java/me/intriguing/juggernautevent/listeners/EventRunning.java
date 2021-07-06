package me.intriguing.juggernautevent.listeners;

import me.intriguing.juggernautevent.Core;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class EventRunning implements Listener {

    private static final Core plugin = Core.getPlugin();

    @EventHandler
    public void handlEventStarted(PlayerJoinEvent e) {
        if (plugin.getEventManager().isRunning()) {
            // TODO: If event is running (checked above), then teleport player
            //  to event arena with spectator mode.
        } else {
            // TODO: Teleport player to waiting area
        }
    }



}
