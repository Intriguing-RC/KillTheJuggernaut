package me.intriguing.juggernautevent.hooks;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.intriguing.juggernautevent.Core;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlaceholderAPIHook extends PlaceholderExpansion {

    public static Core plugin;

    public PlaceholderAPIHook() {
        plugin = Core.getPlugin();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "juggernautevent";
    }

    @Override
    public @NotNull String getAuthor() {
        return Core.getPlugin().getDescription().getAuthors().iterator().next();
    }

    @Override
    public @NotNull String getVersion() {
        return Core.getPlugin().getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if(params.equals("juggernaut")) {
            return plugin.getEventManager().getJuggernaut().getName();
        }

        return null;
    }
}
