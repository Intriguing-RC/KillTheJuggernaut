package me.intriguing.juggernautevent.commands;

import me.intriguing.juggernautevent.Core;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;

public class HelpCommand extends SubCommand {

    FileConfiguration config = Core.getPlugin().getConfig();
    BukkitAudiences bukkitAudiences = Core.getPlugin().adventure();

    @Override
    public String getLabel() {
        return "help";
    }

    @Override
    public void onCommand(Player player, String[] args) {
        Audience audience = bukkitAudiences.player(player);
        if (player.hasPermission("event.help")) {
            List<String> helpMessages = config.getStringList("messages.help");
            for (String helpMessage : helpMessages) {
                audience.sendMessage(MiniMessage.get().parse(helpMessage));
            }
            return;
        }

        audience.sendMessage(MiniMessage.get().parse(Objects.requireNonNull(config.getString("messages.no-permission"))));
    }
}
