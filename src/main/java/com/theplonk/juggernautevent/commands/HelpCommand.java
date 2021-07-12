package com.theplonk.juggernautevent.commands;

import com.theplonk.juggernautevent.managers.SettingsManager;
import com.theplonk.juggernautevent.Core;
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
            audience.sendMessage(MiniMessage.get().parse(config.helpMessage));
            return;
        }

        audience.sendMessage(MiniMessage.get().parse(Objects.requireNonNull(config.noPermissionMessage)));
    }
}
