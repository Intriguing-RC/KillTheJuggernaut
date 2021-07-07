package me.intriguing.juggernautevent.util;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class WordTimer {

    public static String getWords(Duration duration) {
        if (duration.toSeconds() <= 0) {
            return "0 seconds";
        }

        StringBuilder str = new StringBuilder();

        long secondsParsed = duration.toSeconds();
        Map<String, Long> values = new HashMap<>();

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

}
