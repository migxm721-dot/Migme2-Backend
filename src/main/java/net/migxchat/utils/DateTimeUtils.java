package net.migxchat.utils;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
public class DateTimeUtils {
    public long toEpochMilli(LocalDateTime ldt) {
        return ldt.toInstant(ZoneOffset.UTC).toEpochMilli();
    }

    public LocalDateTime fromEpochMilli(long millis) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneOffset.UTC);
    }

    public long now() {
        return System.currentTimeMillis();
    }
}
