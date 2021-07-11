package me.intriguing.juggernautevent.util;

import lombok.Getter;
import me.intriguing.juggernautevent.Core;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nullable;
import java.time.Duration;
import java.util.Set;

public class CountdownTimer implements Runnable {

    @Getter private long secondsLeft;
    @Getter private BukkitTask bukkitTask;
    private final Set<Long> notifyTimes;
    private static Core plugin;
    private final Runnable runAfter;
    private final String broadcastOnNotify;
    private int bukkitTaskId;


    public CountdownTimer(long timer, Set<Long> notifyTimes, @Nullable Runnable runAfter, @Nullable String broadcastOnNotify) {
        if (timer < 0) {
            Bukkit.getLogger().severe("Timer must be greater than or equal to zero!");
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
            this.cancel();
        }

        if (notifyTimes.contains(secondsLeft)) {
            // TODO: Placeholder System
            if (this.broadcastOnNotify != null) {
                plugin.getAdventure().players().sendMessage(
                        MiniMessage.get().parse(broadcastOnNotify, Template.of("secondsleft", TimerUtil.getWords(Duration.ofSeconds(secondsLeft)))));
            }
        }

        // TODO: Add placeholder integration for timer

        secondsLeft--;
    }

    public void start() {
        bukkitTask = Bukkit.getScheduler().runTaskTimer(plugin, this, 0, 20L);
    }

    public void cancel() {
        this.getBukkitTask().cancel();
        if (this.runAfter != null) this.runAfter.run();
    }



}
