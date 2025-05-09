package com.devteria.post.service;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class DateTimeFormatter {
    Map<Long, Function<Instant, String>> strategyMap = new LinkedHashMap<>();

    public DateTimeFormatter() {
        strategyMap.put(60L, this::formatSeconds);
        strategyMap.put(3600L, this::formatMinutes);
        strategyMap.put(86400L, this::formatHours);
        strategyMap.put(Long.MAX_VALUE, this::formatDate);
    }

    public String format(Instant instant) {
        long elapsedSeconds = ChronoUnit.SECONDS.between(instant, Instant.now());

        var strategy = strategyMap.entrySet()
                .stream()
                .filter(entry -> elapsedSeconds < entry.getKey())
                .findFirst().get();

        return strategy.getValue().apply(instant);
    }

    private String formatSeconds(Instant instant) {
        long elapsedSeconds = ChronoUnit.SECONDS.between(instant, Instant.now());
        return elapsedSeconds + " seconds";
    }

    private String formatMinutes(Instant instant) {
        long elapsedMinutes = ChronoUnit.MINUTES.between(instant, Instant.now());
        return elapsedMinutes + " minutes";
    }

    private String formatHours(Instant instant) {
        long elapsedHours = ChronoUnit.HOURS.between(instant, Instant.now());
        return elapsedHours + " hours";
    }

    private String formatDate(Instant instant) {
        LocalDateTime localDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ISO_DATE;

        return localDateTime.format(formatter);
    }
}
