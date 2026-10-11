package edu.byohttp.resource;

import edu.byohttp.mime.MimeTypeMapper;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.Date;
import java.util.Optional;

public final class DirectoryResourceFetcher implements ResourceFetcher {
    private final Path localDirectory;
    private final MimeTypeMapper mimeMapper;
    private static final String PATH_SEPARATOR = "^/";
    private static final int NON_EXISTENT_INDEX = -1;
    private static final char EXTENSION_SEPARATOR = '.';
    private static final String NON_EXISTENT_EXTENSION = "invalid";

    public DirectoryResourceFetcher(final Path localDirectory, final MimeTypeMapper mimeMapper) {
        this.localDirectory = localDirectory.toAbsolutePath().normalize();
        this.mimeMapper = mimeMapper;
    }

    @Override
    public Optional<Resource> fetch(String resourcePath) {
        try {
            final Path normalizedResourcePath = this.localDirectory.resolve(
                    resourcePath.replaceFirst(PATH_SEPARATOR, "")).normalize();

            if (!normalizedResourcePath.startsWith(this.localDirectory)
                    || !Files.exists(normalizedResourcePath)
                    || Files.isDirectory(normalizedResourcePath)) {
                return Optional.empty();
            }

            final Optional<String> mimeType = this.mimeMapper.get(getExtension(normalizedResourcePath));

            return mimeType.isEmpty()
                   ? Optional.empty() : buildResource(normalizedResourcePath, mimeType.get());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getExtension(final Path path) {
        final String fileName = path.getFileName().toString();
        int dotIndex = fileName.lastIndexOf(EXTENSION_SEPARATOR);

        if (dotIndex == NON_EXISTENT_INDEX) {
            return NON_EXISTENT_EXTENSION;
        }

        return fileName.substring(++dotIndex).toLowerCase();
    }

    private Optional<Resource> buildResource(final Path normalizedResourcePath, final String mimeType)
            throws IOException {
        final InputStream body = Files.newInputStream(normalizedResourcePath);
        final long length = Files.size(normalizedResourcePath);
        final FileTime lastModified = Files.getLastModifiedTime(normalizedResourcePath);
        final Date modifiedDate = new Date(lastModified.toMillis());

        return Optional.of(new Resource(
            normalizedResourcePath.toString(),
            mimeType,
            length,
            modifiedDate,
            body)
        );
    }
}
