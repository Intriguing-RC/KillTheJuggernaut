package com.theplonk.juggernautevent.commands;

import com.theplonk.juggernautevent.Core;
import com.theplonk.juggernautevent.managers.EventManager;
import com.theplonk.juggernautevent.managers.SettingsManager;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import org.bukkit.command.CommandSender;

public class CancelCommand extends SubCommand {

    private final Core plugin;
    private final BukkitAudiences adventure;
    private final Audience server;
    private final EventManager event;
    private final SettingsManager config;

    public CancelCommand() {
        plugin = Core.getPlugin();
        adventure =  plugin.getAdventure();
        server = adventure.players();
        event = plugin.getEventManager();
        config = plugin.getSettingsManager();
    }

    @Override
    public String getLabel() {
        return "cancel";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("event.cancel")) {
            adventure.sender(sender).sendMessage(MiniMessage.get().parse(config.noPermissionMessage));
            return;
        }
        if (event.isRunning()) {
            if (event.isGameStarted()) {
                plugin.getAdventure().players().sendMessage(MiniMessage.get().parse(config.announceWinner, Template.of("name", event.getJuggernaut().getName())));
                event.getGameTimer().getBukkitTask().cancel();
            } else {
                event.getTimer().getBukkitTask().cancel();
            }

            server.sendMessage(MiniMessage.get().parse(config.forcedStopped));
            event.gameEnd();
            adventure.sender(sender).sendMessage(MiniMessage.get().parse(config.successfulStop));
        } else {
            adventure.sender(sender).sendMessage(MiniMessage.get().parse(config.eventNotRunning));
        }
    }
}
