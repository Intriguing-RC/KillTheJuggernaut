package com.theplonk.juggernautevent.hooks;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import com.theplonk.juggernautevent.Core;
import com.theplonk.juggernautevent.managers.EventManager;
import com.theplonk.juggernautevent.managers.SettingsManager;
import com.theplonk.juggernautevent.util.TimerUtil;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.joda.time.Duration;

public class PlaceholderAPIHook extends PlaceholderExpansion {


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
        final Core plugin = Core.getPlugin();
        final EventManager event = plugin.getEventManager();
        final SettingsManager config = plugin.getSettingsManager();

        if(params.equals("juggernaut")) {
            if (event.getJuggernaut() != null) {
                return event.getJuggernaut().getName();
            } else {
                return "None";
            }
        }

        if (params.equals("timeleft")) {
            if (event.isGameStarted()) {
                Duration duration = Duration.standardSeconds(event.getGameTimer().getSecondsLeft());
                return TimerUtil.formatDuration(duration);
            } else if (event.isRunning()) {
                return "Awaiting Game To Start";
            } else {
                return "Game Not Running";
            }
        }

        if (params.equals("tag")) {
            if (event.isGameStarted()) {
                if (player == event.getJuggernaut()) {
                     return LegacyComponentSerializer.legacySection().serialize(MiniMessage.get().parse(config.juggernautTag));
                } else {
                    return LegacyComponentSerializer.legacySection().serialize(MiniMessage.get().parse(config.normalTag));
                }
            } else {
                return "";
            }
        }

        return null;
    }
}
