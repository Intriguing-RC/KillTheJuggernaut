package me.intriguing.juggernautevent.commands;

import me.intriguing.juggernautevent.Core;
import me.intriguing.juggernautevent.util.TimerUtil;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.joda.time.Duration;

import java.time.format.DateTimeParseException;

public class StartCommand extends SubCommand {

    private static Core plugin;

    public StartCommand() {
        plugin = Core.getPlugin();
    }

    @Override
    public String getLabel() {
        return "start";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length >= 1) {
            Duration duration;
            try {
                duration = TimerUtil.getJodaFormatter().parsePeriod(args[0]).toStandardDuration();
            } catch (IllegalArgumentException e) {
                plugin.getAdventure().sender(sender).sendMessage(MiniMessage.get().parse("<red>Invalid time."));
                return;
            }

            if (args.length >= 2) {
                if (Bukkit.getPlayerExact(args[1]) != null) {
                    if (!(Bukkit.getOnlinePlayers().size() < 2)) {
                        plugin.getAdventure().sender(sender).sendMessage(MiniMessage.get().parse("<green>Starting Juggernaut Event!"));
                        plugin.getEventManager().initialize(duration, Bukkit.getPlayerExact(args[1]));
                    } else {
                        plugin.getAdventure().sender(sender).sendMessage(MiniMessage.get().parse("<red>There are less than 2 players." + args[1]));
                    }
                } else {
                    plugin.getAdventure().sender(sender).sendMessage(MiniMessage.get().parse("<red>Invalid player " + args[1]));
                }
            } else {
                // Start with random player
                plugin.getAdventure().sender(sender).sendMessage(MiniMessage.get().parse("<green>Starting Juggernaut Event!"));
                plugin.getEventManager().initialize(duration, null);
            }
        } else {
            plugin.getAdventure().sender(sender).sendMessage(MiniMessage.get().parse("<green>You didn't provide a duration!"));
        }
    }
}
