package edu.byohttp.date;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public final class HttpDateFormatter implements DateFormatter {
    private static final String PATTERN = "EEE, dd MMM yyyy HH:mm:ss 'GMT'";
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern(PATTERN, Locale.US)
                             .withZone(ZoneOffset.UTC);

    @Override
    public String format(Date date) {
        return FORMATTER.format(date.toInstant());
    }
}
