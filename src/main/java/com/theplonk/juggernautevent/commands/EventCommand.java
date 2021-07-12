package com.theplonk.juggernautevent.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class EventCommand implements CommandExecutor, TabExecutor {

    private final Map<String, SubCommand> subCommands = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length > 0) {
            String trySubCommand = args[0];
            if (!trySubCommand.equalsIgnoreCase("help") && subCommands.containsKey(trySubCommand.toLowerCase())) {
                subCommands.get(trySubCommand.toLowerCase()).onCommand(sender, Arrays.copyOfRange(args, 1, args.length));
                return true;
            }
        }

        subCommands.get("help").onCommand(sender, null);
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (args.length == 1) {
            return new ArrayList<>(subCommands.keySet());
        }

        return null;
    }

    public void registerSubCommand(SubCommand command) {
        subCommands.put(command.getLabel(), command);
    }
}
