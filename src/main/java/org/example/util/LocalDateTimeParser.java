package org.example.util;

import lombok.NonNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.example.config.Config.DATE_TIME_PATTERN;

public class LocalDateTimeParser {
    public static LocalDateTime parseStartDate(@NonNull String startDate) {
        return LocalDateTime.parse(startDate, DateTimeFormatter.ofPattern(DATE_TIME_PATTERN));
    }

    public static String toString(@NonNull LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN));
    }

}
