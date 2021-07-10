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
import org.joda.time.Duration;

public class StartCommand extends SubCommand {

    private final Core plugin;
    private final Audience server;
    private final BukkitAudiences adventure;
    private final EventManager event;
    private final SettingsManager config;

    public StartCommand() {
        plugin = Core.getPlugin();
        adventure =  plugin.getAdventure();
        event = plugin.getEventManager();
        config = plugin.getSettingsManager();
        server = adventure.players();
    }

    @Override
    public String getLabel() {
        return "start";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Audience audience = adventure.sender(sender);

        if (args.length >= 1) {
            Duration duration;
            try {
                duration = TimerUtil.getJodaFormatter().parsePeriod(args[0]).toStandardDuration();
                if (duration.getStandardMinutes() < 1) {
                     audience.sendMessage(MiniMessage.get().parse(config.gameMustLastAtLeastOneMinute));
                    return;
                }
            } catch (IllegalArgumentException e) {
                 audience.sendMessage(MiniMessage.get().parse(config.invalidTimeProvided));
                return;
            }

            if (args.length >= 2) {
                if (Bukkit.getPlayerExact(args[1]) != null) {
                    if (!(Bukkit.getOnlinePlayers().size() < 2)) {
                         audience.sendMessage(MiniMessage.get().parse(config.successfulStart));
                         event.initialize(duration, Bukkit.getPlayerExact(args[1]));
                    } else {
                         audience.sendMessage(MiniMessage.get().parse(config.notEnoughPlayers));
                    }
                } else {
                     audience.sendMessage(MiniMessage.get().parse(config.invalidPlayer, Template.of("player", args[1])));
                }
            } else {
                // Start with random player
                audience.sendMessage(MiniMessage.get().parse(config.successfulStart));
                event.initialize(duration, null);
            }
        } else {
             audience.sendMessage(MiniMessage.get().parse(config.invalidTimeProvided));
        }
    }
}
