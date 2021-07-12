package me.intriguing.juggernautevent.util;

import lombok.Getter;
import me.intriguing.juggernautevent.Core;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;
import org.joda.time.Duration;

import java.util.Set;
import java.util.function.Predicate;

public class CountdownTimer implements Runnable {

    @Getter private long secondsLeft;
    @Getter private BukkitTask bukkitTask;
    private final Set<Long> notifyTimes;
    private static Core plugin;
    private final Runnable runAfter;
    private final String broadcastOnNotify;
    private final Predicate<Duration> canSend;

    public CountdownTimer(long timer, @Nullable Set<Long> notifyTimes, @Nullable Runnable runAfter, @Nullable String broadcastOnNotify, @Nullable Predicate<Duration> canSend) {
        if (timer < 0) {
            Bukkit.getLogger().severe("Timer must be greater than or equal to zero!");
        }

        plugin = Core.getPlugin();
        this.runAfter = runAfter;
        this.canSend = canSend;
        this.broadcastOnNotify = broadcastOnNotify;
        this.notifyTimes = notifyTimes;
        this.secondsLeft = timer;
    }

    @Override
    public void run() {
        if (secondsLeft == 0) {
            this.cancelAndRun();
        }

        if (notifyTimes != null) {
            if (notifyTimes.contains(secondsLeft)) {
                if (this.broadcastOnNotify != null) {
                    plugin.getAdventure().players().sendMessage(
                            MiniMessage.get().parse(broadcastOnNotify, Template.of("secondsleft", TimerUtil.getWords(Duration.standardSeconds(secondsLeft)))));
                }
            }
        }

        if (canSend != null) {
            if (canSend.test(Duration.standardSeconds(secondsLeft))) {
                if (this.broadcastOnNotify != null) {
                    plugin.getAdventure().players().sendMessage(
                            MiniMessage.get().parse(broadcastOnNotify, Template.of("secondsleft", TimerUtil.getWords(Duration.standardSeconds(secondsLeft)))));
                }
            }
        }

        secondsLeft--;
    }

    public void start() {
        bukkitTask = Bukkit.getScheduler().runTaskTimer(plugin, this, 0, 20L);
    }

    public void cancelAndRun() {
        this.getBukkitTask().cancel();
        if (this.runAfter != null) this.runAfter.run();
    }



}
