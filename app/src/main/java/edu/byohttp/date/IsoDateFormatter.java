package edu.byohttp.date;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public final class IsoDateFormatter implements DateFormatter {
    private static final ZoneId ZONE = ZoneId.of("UTC");
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    @Override
    public String format(Date date) {
        final Instant instant = date.toInstant();
        final ZonedDateTime zoned = instant.atZone(ZONE);
        return ISO_FORMATTER.format(zoned);
    }
}
