package com.leo.cse.frontend.format;

public class DurationFormatter {
    private final StringBuilder sb = new StringBuilder();

    public synchronized String format(long duration) {
        sb.setLength(0);

        final long minutes   = duration / 3600L;
        final long seconds = (duration - (minutes * 3600)) / 60;
        final long centiseconds = (duration - (minutes * 3600) - (seconds * 60)) * 100 / 600;

        if (minutes < 10) {
            sb.append('0');
        }
        sb.append(minutes).append(':');

        if (seconds < 10) {
            sb.append('0');
        }
        sb.append(seconds);

        if (centiseconds > 0) {
            sb.append('.');
            sb.append(centiseconds);
        }

        return sb.toString();
    }
}
