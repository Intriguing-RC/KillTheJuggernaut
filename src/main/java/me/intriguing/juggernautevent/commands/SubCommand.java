package me.intriguing.juggernautevent.commands;

import org.bukkit.entity.Player;

public abstract class SubCommand  {

    public abstract String getLabel();
    public abstract void onCommand(Player player, String[] args);

}
