package me.intriguing.juggernautevent.commands;

import org.bukkit.command.CommandSender;

public abstract class SubCommand  {

    public abstract String getLabel();
    public abstract void onCommand(CommandSender sender, String[] args);

}
