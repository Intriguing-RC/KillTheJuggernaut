package me.intriguing.juggernautevent.commands;

import me.intriguing.juggernautevent.Core;
import me.intriguing.juggernautevent.managers.EventManager;
import me.intriguing.juggernautevent.managers.SettingsManager;
import me.intriguing.juggernautevent.util.TimerUtil;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.joda.time.Duration;

import java.util.ArrayList;
import java.util.List;

public class StartCommand extends SubCommand {

    private final Core plugin;
    private final BukkitAudiences adventure;
    private final EventManager event;
    private final SettingsManager config;

    public StartCommand() {
        plugin = Core.getPlugin();
        adventure =  plugin.getAdventure();
        event = plugin.getEventManager();
        config = plugin.getSettingsManager();
    }

    @Override
    public String getLabel() {
        return "start";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Audience audience = adventure.sender(sender);

        if (!sender.hasPermission("event.start")) {
            audience.sendMessage(MiniMessage.get().parse(config.noPermissionMessage));
            return;
        }

        if (event.isRunning()) {
            audience.sendMessage((MiniMessage.get().parse(config.eventAlreadyRunning)));
            return;
        }

        if (args.length >= 1) {
            Duration duration;
            try {
                duration = TimerUtil.getJodaFormatter().parsePeriod(args[0]).toStandardDuration();
            } catch (IllegalArgumentException e) {
                audience.sendMessage(MiniMessage.get().parse(config.invalidTimeProvided));
                return;
            }

            List<Player> playingList = new ArrayList<>(Bukkit.getOnlinePlayers());
            Bukkit.getOnlinePlayers().forEach(player -> {
                if (player.hasPermission("event.exempt")) {
                    playingList.remove(player);
                }
            });


            if (args.length >= 2) {
                if (Bukkit.getPlayerExact(args[1]) != null) {
                    if (!playingList.contains(Bukkit.getPlayerExact(args[1]))) {
                        audience.sendMessage(MiniMessage.get().parse(config.invalidPlayerExempt));
                        return;
                    }
                    if (!(playingList.size() < 2)) {
                         audience.sendMessage(MiniMessage.get().parse(config.successfulStart));
                         event.initialize(duration, Bukkit.getPlayerExact(args[1]), playingList);
                    } else {
                         audience.sendMessage(MiniMessage.get().parse(config.notEnoughPlayers));
                    }
                } else {
                     audience.sendMessage(MiniMessage.get().parse(config.invalidPlayer, Template.of("player", args[1])));
                }
            } else {
                // Start with random player
                if (!(playingList.size() < 2)) {
                    audience.sendMessage(MiniMessage.get().parse(config.successfulStart));
                    event.initialize(duration, null, playingList);
                } else {
                    audience.sendMessage(MiniMessage.get().parse(config.notEnoughPlayers));
                }
            }
        } else {
             audience.sendMessage(MiniMessage.get().parse(config.invalidTimeProvided));
        }
    }
}
