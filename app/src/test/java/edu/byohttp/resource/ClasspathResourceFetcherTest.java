package edu.byohttp.resource;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class ClasspathResourceFetcherTest {
    private static final String ERROR_FILE = "/400.html";
    private static final Path ERROR_DIRECTORY = Path.of("build", "resources", "main");
    private static final String PATH_SEPARATOR = "^/";
    private static final String CONTENT_SEPARATOR = "\\s+";
    private static final String MIME_TYPE_HTML = "text/html";

    private static ClasspathResourceFetcher fetcher;

    @BeforeAll
    static void setup() {
        fetcher = new ClasspathResourceFetcher();
    }

    @Test
    void fetchValid() throws IOException {
        // given
        final String htmlContent = """
            <!DOCTYPE html>
            <html>
            <head>
                <title>400 Bad Request</title>
            </head>
            <body>
                <center><h1>400 Bad Request</h1></center>
                <hr><center>byohttp/0.0.1</center>
            </body>
            </html>
        """;

        final Path htmlFile = ERROR_DIRECTORY.
                resolve(ERROR_FILE.replaceFirst(PATH_SEPARATOR, ""));

        // when
        final Optional<Resource> optionalResource = fetcher.fetch(ERROR_FILE);

        // then
        assertTrue(optionalResource.isPresent());
        final Resource resource = optionalResource.get();

        assertEquals(MIME_TYPE_HTML, resource.mimeType());
        assertEquals(
                htmlContent.replaceAll(CONTENT_SEPARATOR, "").trim(),
                new String(resource.body().readAllBytes()).
                replaceAll(CONTENT_SEPARATOR, "").trim());
        assertEquals(
                htmlFile.toAbsolutePath().normalize(),
                Path.of(resource.path()).toAbsolutePath().normalize()
        );
        assertTrue(resource.length() > 0);
        assertNotNull(resource.lastModified());
    }

    @Test
    void fetchFileNotFound() {
        // given
        final String nonExistentFile = "/notfound.html";

        // when
        final Optional<Resource> optionalResource = fetcher.fetch(nonExistentFile);

        // then
        assertTrue(optionalResource.isEmpty());
    }

}
