package com.securelink.ssh.util;

import java.util.concurrent.TimeUnit;

public class Utils {
    public static String fmt(String template, Object... args) {
        return String.format(template, args);
    }

    public static String fmt(long ms) {
        return String.format("%02d:%02d:%02d.%02d",
            TimeUnit.MILLISECONDS.toHours(ms),
            TimeUnit.MILLISECONDS.toMinutes(ms) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(ms)),
            TimeUnit.MILLISECONDS.toSeconds(ms) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(ms)),
            TimeUnit.MILLISECONDS.toMillis(ms) - TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(ms))
        );
    }
}
