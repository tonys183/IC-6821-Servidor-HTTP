package edu.byohttp.mime;

import java.nio.file.Path;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class CsvMimeTypeMapperTest {

    private static MimeTypeMapper csvMimeTypeMapper;
    private static final Path MAPPINGS_CSV = Path.of("..", "mappings", "mimetype.csv");
    private static final String EXTENSION_AAC = "aac";
    private static final String MIME_AAC = "audio/aac";
    private static final String EXTENSION_7Z = "7z";
    private static final String MIME_7Z = "application/x-7z-compressed";
    private static final String EXTENSION_PPTX = "pptx";
    private static final String MIME_PPTX =
            "application/vnd.openxmlformats-officedocument.presentationml.presentation";

    @BeforeEach
    public void setup() {
        csvMimeTypeMapper = new CsvMimeTypeMapper();
    }

    @Test
    void mapValidTest() {
        // given
        csvMimeTypeMapper.map(MAPPINGS_CSV.toString());

        // when
        final String[] firstExtensionAndMimeType =
                {EXTENSION_AAC,
                MIME_AAC,
                };

        final String[] lastExtensionAndMimeType =
                {EXTENSION_7Z,
                MIME_7Z,
                };

        final String[] middleExtensionAndMimeType =
                {EXTENSION_PPTX,
                MIME_PPTX,
                };

        final Optional<String> firstMimeType = csvMimeTypeMapper.get(firstExtensionAndMimeType[0]);
        final Optional<String> lastMimeType = csvMimeTypeMapper.get(lastExtensionAndMimeType[0]);
        final Optional<String> middleMimeType = csvMimeTypeMapper.get(middleExtensionAndMimeType[0]);

        // then
        assertTrue(firstMimeType.isPresent());
        assertTrue(lastMimeType.isPresent());
        assertTrue(middleMimeType.isPresent());
        assertEquals(firstExtensionAndMimeType[1], firstMimeType.get());
        assertEquals(lastExtensionAndMimeType[1], lastMimeType.get());
        assertEquals(middleExtensionAndMimeType[1], middleMimeType.get());
    }

    @Test
    void mapNotFoundPathTest() {
        // given
        final String nonExistentPath = "example/error.csv";

        // when

        // then
        assertThrows(RuntimeException.class, () -> csvMimeTypeMapper.map(nonExistentPath));
    }

    @Test
    void getExistentMimeTest() {
        // given
        csvMimeTypeMapper.map(MAPPINGS_CSV.toString());
        final String validExtension = "txt";
        final String txtMimeType = "text/plain";

        // when
        final Optional<String> mimeType = csvMimeTypeMapper.get(validExtension);

        // then
        assertTrue(mimeType.isPresent());
        assertEquals(txtMimeType, mimeType.get());
    }

    @Test
    void getCaseInsensitiveTest() {
        // given
        csvMimeTypeMapper.map(MAPPINGS_CSV.toString());
        final String validExtension = "TxT";
        final String txtMimeType = "text/plain";

        // when
        final Optional<String> mimeType = csvMimeTypeMapper.get(validExtension);

        // then
        assertTrue(mimeType.isPresent());
        assertEquals(txtMimeType, mimeType.get());
    }

    @Test
    void getNonExistentTest() {
        // given
        csvMimeTypeMapper.map(MAPPINGS_CSV.toString());
        final String invalidExtension = "invalid";

        // when
        final Optional<String> nonExistentMimeType = csvMimeTypeMapper.get(invalidExtension);

        // then
        assertTrue(nonExistentMimeType.isEmpty());
    }
}
