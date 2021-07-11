package me.intriguing.juggernautevent.util;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.joda.time.Duration;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class TimerUtil {

    public static String getWords(Duration duration) {
        if (duration.getStandardSeconds() <= 0) {
            return "0 seconds";
        }

        StringBuilder str = new StringBuilder();

        long secondsParsed = duration.getStandardSeconds();
        Map<String, Long> values = new LinkedHashMap<>();

        if (secondsParsed / 86400 >= 1) {
            if (secondsParsed / 86400 == 1) {
                values.put("day", secondsParsed / 86400);
            } else {
                values.put("days", secondsParsed / 86400);
            }
            secondsParsed -= 86400 * (secondsParsed / 86400);
        }

        if (secondsParsed / 3600 >= 1) {
            if (secondsParsed / 3600 == 1) {
                values.put("hour", secondsParsed / 3600);
            } else {
                values.put("hours", secondsParsed / 3600);
            }
            secondsParsed -= 3600 * (secondsParsed / 3600);
        }

        if (secondsParsed / 60 >= 1) {
            if (secondsParsed / 60 == 1) {
                values.put("minute", secondsParsed / 60);
            } else {
                values.put("minutes", secondsParsed / 60);
            }
            secondsParsed -= 60 * (secondsParsed / 60);
        }

        if (secondsParsed >= 1) {
            if (secondsParsed == 1) {
                values.put("second", secondsParsed);
            } else {
                values.put("seconds", secondsParsed);
            }
        }

        values.forEach((k, v) -> {
            if (v != 0) {
                str.append(v).append(" ").append(k).append(" ");
            }
        });

        return str.toString().trim();

    }

    public static PeriodFormatter getJodaFormatter() {
        return new PeriodFormatterBuilder()
                .appendHours().appendSuffix("h")
                .appendMinutes().appendSuffix("m")
                .appendSeconds().appendSuffix("s")
                .toFormatter();
    }

    public static String formatDuration(Duration duration) {
        return DurationFormatUtils.formatDuration(duration.getMillis(), "HH:mm:ss", true);
    }

    public static boolean automaticRemind(Duration duration) {
        long seconds = duration.getStandardSeconds();

        if (seconds <= 60) {
            return (Arrays.asList(60L, 30L, 15L, 10L, 5L, 4L, 3L, 2L, 1L).contains(seconds));
        }

        if (seconds <= 3600) {
            double hourTest = (seconds / 60.0) - (int) (seconds / 60.0);
            return (Arrays.asList(0.0d, 0.5d).contains(hourTest));
        }

        else {
            double multiHourTest = (seconds / 3600.0) - (int) (seconds / 3600.0);
            if (Arrays.asList(0.0d, 0.5d).contains(multiHourTest)) return true;
        }

        return false;
    }

}
