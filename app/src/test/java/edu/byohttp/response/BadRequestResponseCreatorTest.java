package edu.byohttp.response;

import edu.byohttp.date.DateFormatter;
import edu.byohttp.date.HttpDateFormatter;
import edu.byohttp.resource.ClasspathResourceFetcher;
import edu.byohttp.resource.Resource;
import edu.byohttp.resource.ResourceFetcher;
import edu.byohttp.response.mapper.HttpStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Optional;

public final class BadRequestResponseCreatorTest {
    private static ResourceFetcher fetcher;
    private static DateFormatter formatter;
    private static ResponseCreator controller;

    private static final String ERROR_FILE = "/400.html";
    private static final String HTTP_VERSION = "HTTP/1.1";
    private static final String HEADER_CONTENT_TYPE_KEY = "Content-Type";
    private static final String HEADER_CONTENT_TYPE_VALUE = "text/html";
    private static final String HEADER_CONTENT_LENGTH_KEY = "Content-Length";

    @BeforeAll
    static void setup() {
        fetcher = new ClasspathResourceFetcher();
        formatter = new HttpDateFormatter();
        controller = new BadRequestResponseCreator(fetcher, formatter);
    }

    @Test
    public void createValidTest() {
        // given
        final Optional<Resource> optionalResource = fetcher.fetch(ERROR_FILE);

        // when
        final Response response = controller.create(HTTP_VERSION);

        // then
        assertTrue(optionalResource.isPresent());
        assertEquals(HTTP_VERSION, response.httpVersion());
        assertEquals(HttpStatus.BAD_REQUEST, response.httpStatus());
        assertEquals(HEADER_CONTENT_TYPE_VALUE, response.headers().get(HEADER_CONTENT_TYPE_KEY));
        assertEquals(
                String.valueOf(optionalResource.get().length()),
                response.headers().get(HEADER_CONTENT_LENGTH_KEY)
        );
    }
}
