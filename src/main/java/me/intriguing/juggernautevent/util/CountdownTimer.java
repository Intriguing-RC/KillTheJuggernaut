package me.intriguing.juggernautevent.util;

import me.intriguing.juggernautevent.Core;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import org.bukkit.Bukkit;

import javax.annotation.Nullable;
import java.time.Duration;
import java.util.Set;

public class CountdownTimer implements Runnable {

    private long secondsLeft;
    private final Set<Long> notifyTimes;
    private static Core plugin;
    private final Runnable runAfter;
    private final String broadcastOnNotify;
    private int bukkitTaskId;

    public CountdownTimer(long timer, Set<Long> notifyTimes, @Nullable Runnable runAfter, @Nullable String broadcastOnNotify) {
        if (timer < 0) {
            throw new IndexOutOfBoundsException("Timer must be greater than or equal to zero!");
        }

        plugin = Core.getPlugin();
        this.runAfter = runAfter;
        this.broadcastOnNotify = broadcastOnNotify;
        this.notifyTimes = notifyTimes;
        this.secondsLeft = timer;
    }

    @Override
    public void run() {
        if (secondsLeft == 0) {
            Bukkit.getScheduler().cancelTask(bukkitTaskId);
            if (this.runAfter != null) this.runAfter.run();
        }

        if (notifyTimes.contains(secondsLeft)) {
            // TODO: Placeholder System
            if (this.broadcastOnNotify != null) {
                plugin.getAdventure().players().sendMessage(
                        MiniMessage.get().parse(broadcastOnNotify, Template.of("secondsleft", String.valueOf(secondsLeft))));
            }
        }

        // TODO: Add placeholder integration for timer

        secondsLeft--;
    }

    public void start() {
        bukkitTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 0, 20L);
    }

}
