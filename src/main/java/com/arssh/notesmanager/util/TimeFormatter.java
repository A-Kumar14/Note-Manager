package com.arssh.notesmanager.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeFormatter {
    private static final DateTimeFormatter ABSOLUTE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy h:mm a");

    public static String formatRelativeTime(LocalDateTime dateTime) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration;
        boolean isPast = dateTime.isBefore(now);

        if (isPast) {
            duration = Duration.between(dateTime, now);
        } else {
            duration = Duration.between(now, dateTime);
        }

        long minutes = duration.toMinutes();
        long hours = duration.toHours();
        long days = duration.toDays();

        String relativeTime;
        if (days > 0) {
            relativeTime = days + " day" + (days > 1 ? "s" : "");
        } else if (hours > 0) {
            relativeTime = hours + " hour" + (hours > 1 ? "s" : "");
        } else {
            relativeTime = minutes + " minute" + (minutes > 1 ? "s" : "");
        }

        if (isPast) {
            return relativeTime + " ago";
        } else {
            return "In " + relativeTime;
        }
    }

    public static String formatAbsoluteTime(LocalDateTime dateTime) {
        return dateTime.format(ABSOLUTE_FORMATTER);
    }

    public static String formatBoth(LocalDateTime dateTime) {
        boolean isPast = dateTime.isBefore(LocalDateTime.now());

        if (isPast) {
            return "OVERDUE: Was " + formatAbsoluteTime(dateTime);
        } else {
            return formatRelativeTime(dateTime) + " (" + formatAbsoluteTime(dateTime) + ")";
        }
    }
}
