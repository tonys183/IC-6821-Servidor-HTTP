package edu.byohttp.date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class HttpDateFormatterTest {
    private HttpDateFormatter formatter;
    private static final String DATE_ISO_TEST = "2025-10-20T15:00:00Z";

    @BeforeEach
    public void setup() {
        formatter = new HttpDateFormatter();
    }

    @Test
    void formatValidDateTest() {
        // given
        final String expectedDate = "Mon, 20 Oct 2025 15:00:00 GMT";
        final Date date = Date.from(Instant.parse(DATE_ISO_TEST));

        // when
        final String formattedDate = formatter.format(date);

        // then
        assertEquals(expectedDate, formattedDate);
    }

    @Test
    void formatToEnglishTest() {
        // given
        final Date date = Date.from(Instant.parse(DATE_ISO_TEST));
        final String expectedDayFormat = "Mon";
        final String expectedMonthFormat = "Oct";
        final String expectedTimeZoneFormat = "GMT";


        // when
        final String formattedDate = formatter.format(date);

        // then
        assertTrue(formattedDate.endsWith(expectedTimeZoneFormat));
        assertTrue(formattedDate.contains(expectedMonthFormat));
        assertTrue(formattedDate.startsWith(expectedDayFormat));
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
