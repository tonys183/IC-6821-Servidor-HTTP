package edu.byohttp.mime;

import java.util.Optional;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class CsvMimeTypeMapper implements MimeTypeMapper {
    private final Map<String, String> mimeTypes;
    private static final String CSV_SEPARATOR = ";";
    private static final int EXTENSION = 0;
    private static final int MIME_TYPE = 1;
    private static final int MAXIMUM_LINE_LENGTH = 2;

    public CsvMimeTypeMapper() {
        this.mimeTypes = new HashMap<>();
    }

    @Override
    public void map(final String path) {
        this.mimeTypes.clear();

        try (BufferedReader rawCSV = new BufferedReader(new FileReader(path))) {
            String csvLine;
            while ((csvLine = rawCSV.readLine()) != null) {
                final String[] csvLineParts = csvLine.split(CSV_SEPARATOR, MAXIMUM_LINE_LENGTH);
                final String extension = csvLineParts[EXTENSION].trim().toLowerCase();
                final String mimeType = csvLineParts[MIME_TYPE].trim().toLowerCase();
                this.mimeTypes.put(extension, mimeType);

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<String> get(final String extension) {
        final String normalizeExtension = extension.trim().toLowerCase();
        return Optional.ofNullable(this.mimeTypes.get(normalizeExtension));
    }
}
