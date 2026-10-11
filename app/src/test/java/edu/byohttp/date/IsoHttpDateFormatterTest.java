package edu.byohttp.date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class IsoHttpDateFormatterTest {
    private IsoDateFormatter formatter;
    private static final String DATE_ISO_TEST = "2025-10-20T15:00:00Z";
    private static final String REGEXP_VALID_DATE_FORMAT = ".*([+-]\\d{2}:\\d{2}|Z)$";

    @BeforeEach
    public void setup() {
        formatter = new IsoDateFormatter();
    }

    @Test
    void formatValidDateTest() {
        // given
        final Date date = Date.from(Instant.parse(DATE_ISO_TEST));
        final String expectedDate = "2025-10-20T15:00:00Z";

        // when
        final String formattedDate = formatter.format(date);

        // then
        assertEquals(expectedDate, formattedDate);
    }

    @Test
    void formatContainsDatePartsTest() {
        // given
        final Date date = Date.from(Instant.parse(DATE_ISO_TEST));
        final String expectedDateFormat = "2025-10-20";
        final String expectedCostaRicaTimeZone = "T15";
        final String expectedAmericanTimeZone = "T09";

        // when
        final String formattedDate = formatter.format(date);

        // then
        assertTrue(formattedDate.startsWith(expectedDateFormat));
        assertTrue(formattedDate.contains(expectedCostaRicaTimeZone)
                || formattedDate.contains(expectedAmericanTimeZone));
        assertTrue(formattedDate.matches(REGEXP_VALID_DATE_FORMAT));
    }

    @Test
    void formatNullDateTest() {
        // given
        final Date date = null;

        // when

        // then
        assertThrows(NullPointerException.class, () -> formatter.format(date));
    }

}
