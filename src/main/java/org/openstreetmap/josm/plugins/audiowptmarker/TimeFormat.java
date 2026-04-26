package org.openstreetmap.josm.plugins.audiowptmarker;

import java.util.Locale;

final class TimeFormat {
    private TimeFormat() {
    }

    static String formatDuration(double seconds) {
        if (!Double.isFinite(seconds) || seconds < 0) {
            return "";
        }
        long totalMillis = Math.round(seconds * 1000.0);
        long totalSeconds = totalMillis / 1000;
        long millis = totalMillis % 1000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long secs = totalSeconds % 60;
        if (millis == 0) {
            return hours > 0
                    ? String.format(Locale.ROOT, "%d:%02d:%02d", hours, minutes, secs)
                    : String.format(Locale.ROOT, "%d:%02d", minutes, secs);
        }
        return hours > 0
                ? String.format(Locale.ROOT, "%d:%02d:%02d.%03d", hours, minutes, secs, millis)
                : String.format(Locale.ROOT, "%d:%02d.%03d", minutes, secs, millis);
    }
}
