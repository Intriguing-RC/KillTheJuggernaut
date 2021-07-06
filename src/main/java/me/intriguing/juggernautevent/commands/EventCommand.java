package me.intriguing.juggernautevent.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class EventCommand implements CommandExecutor {

    private final Map<String, SubCommand> subCommands = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length > 0) {
                String trySubCommand = args[0];
                if (!trySubCommand.equalsIgnoreCase("help") && subCommands.containsKey(trySubCommand.toLowerCase())) {
                    subCommands.get(trySubCommand.toLowerCase()).onCommand(player, Arrays.copyOfRange(args, 1, args.length));
                }
            }

            subCommands.get("help").onCommand(player, null);
            return true;
        }

        return false;
    }

    public void registerSubCommand(SubCommand command) {
        subCommands.put(command.getLabel(), command);
    }
}
