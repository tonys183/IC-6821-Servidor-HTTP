package edu.byohttp.resource;

import edu.byohttp.mime.CsvMimeTypeMapper;
import edu.byohttp.mime.MimeTypeMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class DirectoryResourceFetcherTest {
    private static DirectoryResourceFetcher fetcher;
    private static final String HELLO_WORLD_FILE = "/helloworld.html";
    private static final String MIME_TYPE_HTML = "text/html";
    private static final Path LOCAL_DIRECTORY = Path.of("..", "www");
    private static final String PATH_SEPARATOR = "^/";
    private static final Path MAPPING_DIRECTORY = Path.of("..", "mappings", "mimetype.csv");

    @BeforeAll
    static void setup() {
        final MimeTypeMapper mimeTypeMapper = new CsvMimeTypeMapper();
        mimeTypeMapper.map(MAPPING_DIRECTORY.toString());
        fetcher = new DirectoryResourceFetcher(LOCAL_DIRECTORY, mimeTypeMapper);
    }

    @Test
    void fetchValid() throws IOException {
        // given
        final String htmlContent = """
            <html>
            <head>
                <title>Hello world!</title>
            </head>
            <body>
                <h1>Hello world!</h1>
            </body>
            </html>
        """;

        final Path htmlFile = LOCAL_DIRECTORY.
                resolve(HELLO_WORLD_FILE.replaceFirst(PATH_SEPARATOR, ""));

        // when
        Files.writeString(htmlFile, htmlContent);
        final Optional<Resource> optionalResource = fetcher.fetch(HELLO_WORLD_FILE);

        // then
        assertTrue(optionalResource.isPresent());
        final Resource resource = optionalResource.get();

        assertEquals(MIME_TYPE_HTML, resource.mimeType());
        assertEquals(htmlContent.trim(), new String(resource.body().readAllBytes()).trim());
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

    @Test
    void fetchMimeTypeNotFound() throws IOException {
        // given
        final String htmlContent = "not a valid mime type file content";

        final String nonExistentExtension = "/helloworld";
        final Path htmlFile = LOCAL_DIRECTORY.
                resolve(nonExistentExtension.replaceFirst(PATH_SEPARATOR, ""));

        // when
        Files.writeString(htmlFile, htmlContent);
        final Optional<Resource> optionalResource = fetcher.fetch(nonExistentExtension);

        // then
        assertTrue(optionalResource.isEmpty());
        Files.delete(htmlFile);
    }

    @Test
    void fetchException() throws IOException {
        // given
        final String unreadableFile = "locked.txt";
        final Path unreadable = LOCAL_DIRECTORY.
                resolve(unreadableFile.replaceFirst(PATH_SEPARATOR, ""));

        // when
        Files.writeString(unreadable, "content");
        unreadable.toFile().setReadable(false);

        // then
        try {
            assertThrows(RuntimeException.class, () -> fetcher.fetch(unreadableFile));
        } finally {
            unreadable.toFile().setReadable(true);
            Files.deleteIfExists(unreadable);
        }
    }
}
