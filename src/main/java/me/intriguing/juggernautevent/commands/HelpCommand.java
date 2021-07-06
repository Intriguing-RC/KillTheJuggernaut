package me.intriguing.juggernautevent.commands;

import me.intriguing.juggernautevent.Core;
import me.intriguing.juggernautevent.managers.SettingsManager;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;

import java.util.Objects;

public class HelpCommand extends SubCommand {

    SettingsManager config = Core.getPlugin().getSettingsManager();
    BukkitAudiences bukkitAudiences = Core.getPlugin().getAdventure();

    @Override
    public String getLabel() {
        return "help";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Audience audience = bukkitAudiences.sender(sender);
        if (sender.hasPermission("event.help")) {
            for (String helpMessage : config.helpMessage) {
                audience.sendMessage(MiniMessage.get().parse(helpMessage));
            }
            return;
        }

        audience.sendMessage(MiniMessage.get().parse(Objects.requireNonNull(config.noPermissionMessage)));
    }
}
