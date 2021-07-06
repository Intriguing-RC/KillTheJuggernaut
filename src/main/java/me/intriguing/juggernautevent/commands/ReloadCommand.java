package me.intriguing.juggernautevent.commands;

import me.intriguing.juggernautevent.Core;
import me.intriguing.juggernautevent.managers.SettingsManager;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;

public class ReloadCommand extends SubCommand {

    private static SettingsManager config;
    private final BukkitAudiences adventure;

    public ReloadCommand() {
         config = Core.getPlugin().getSettingsManager();
         adventure = Core.getPlugin().getAdventure();
    }

    @Override
    public String getLabel() {
        return "reload";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Audience audience = adventure.sender(sender);
        if (config.reloadConfiguration()) {
            audience.sendMessage(MiniMessage.get().parse(config.reloadMessage));
        } else {
            audience.sendMessage(MiniMessage.get().parse(config.failedReloadMessage));
        }
    }
}
