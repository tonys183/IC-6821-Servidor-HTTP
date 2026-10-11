package edu.byohttp.resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.Date;
import java.util.Optional;

public final class ClasspathResourceFetcher implements ResourceFetcher {
    private static final String MIME_TYPE = "text/html";
    private static final String PATH_SEPARATOR = "^/";

    @Override
    public Optional<Resource> fetch(String resourcePath) {
        try {
            final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            final Optional<URL> resourceUrl = Optional.ofNullable(
                    classLoader.getResource(resourcePath.replaceFirst(PATH_SEPARATOR, ""))
            );

            if (resourceUrl.isEmpty()) {
                return Optional.empty();
            }

            final Path resourceFile = Paths.get(resourceUrl.get().toURI());

            if (!Files.exists(resourceFile) || Files.isDirectory(resourceFile)) {
                return Optional.empty();
            }

            return buildResource(resourceFile);

        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<Resource> buildResource(final Path normalizedResourcePath)
            throws IOException {
        final InputStream body = Files.newInputStream(normalizedResourcePath);
        final long length = Files.size(normalizedResourcePath);
        final FileTime lastModified = Files.getLastModifiedTime(normalizedResourcePath);
        final Date modifiedDate = new Date(lastModified.toMillis());

        return Optional.of(new Resource(
            normalizedResourcePath.toString(),
            MIME_TYPE,
            length,
            modifiedDate,
            body)
        );
    }
}
