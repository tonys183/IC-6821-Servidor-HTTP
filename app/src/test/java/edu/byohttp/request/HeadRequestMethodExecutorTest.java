package edu.byohttp.request;

import edu.byohttp.date.DateFormatter;
import edu.byohttp.date.HttpDateFormatter;
import edu.byohttp.mime.CsvMimeTypeMapper;
import edu.byohttp.mime.MimeTypeMapper;
import edu.byohttp.request.parser.HttpRequestParser;
import edu.byohttp.request.parser.SplitHttpRequestParser;
import edu.byohttp.resource.DirectoryResourceFetcher;
import edu.byohttp.resource.Resource;
import edu.byohttp.resource.ResourceFetcher;
import edu.byohttp.response.Response;
import edu.byohttp.response.mapper.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class HeadRequestMethodExecutorTest {
    private static final String HELLO_WORLD_FILE = "/helloworld.html";
    private static final String SERVER_HEADER_KEY = "Server";
    private static final String SERVER_HEADER_VALUE = "byohttp/0.0.1";
    private static final String CONTENT_TYPE_HEADER_KEY = "Content-Type";
    private static final String CONTENT_LENGTH_HEADER_KEY = "Content-Length";
    private static final Path LOCAL_DIRECTORY = Path.of("..", "www");
    private static final Path MAPPING_DIRECTORY = Path.of("..", "mappings", "mimetype.csv");

    private static RequestMethodExecutor executor;
    private static DateFormatter formatter;
    private static ResourceFetcher fetcher;
    private static MimeTypeMapper mimeTypeMapper;
    private static HttpRequestParser parser;

    @BeforeEach
    void setup() {
        parser = new SplitHttpRequestParser();
        formatter = new HttpDateFormatter();
        mimeTypeMapper = new CsvMimeTypeMapper();
        mimeTypeMapper.map(MAPPING_DIRECTORY.toString());
        fetcher = new DirectoryResourceFetcher(LOCAL_DIRECTORY, mimeTypeMapper);
        executor = new HeadRequestMethodExecutor(fetcher, formatter);
    }

    @Test
    void executeTest() {
        // given
        final String unparsedRequest = """
        HEAD /helloworld.html HTTP/1.1
        User-Agent: PostmanRuntime/7.22.0
        Accept: */*
        Cache-Control: no-cache
        Host: localhost
        Accept-Encoding: gzip, deflate, br
        Connection: keep-alive
        """;
        final Optional<Request> request = parser.parse(unparsedRequest);

        // when
        final Optional<Response> headResponse = executor.execute(request.get());
        final Optional<Resource> resource = fetcher.fetch(HELLO_WORLD_FILE);

        // then
        assertTrue(headResponse.isPresent());
        assertTrue(resource.isPresent());
        assertEquals(HttpStatus.OK, headResponse.get().httpStatus());
        assertEquals(request.get().httpVersion(), headResponse.get().httpVersion());

        assertEquals(SERVER_HEADER_VALUE,
                headResponse.get().headers().get(SERVER_HEADER_KEY));

        assertEquals(headResponse.get().headers().get(CONTENT_TYPE_HEADER_KEY),
                resource.get().mimeType());

        assertEquals(headResponse.get().headers().get(CONTENT_LENGTH_HEADER_KEY),
                String.valueOf(resource.get().length()));

    }

    @Test
    void executeIncorrectTest() {
        // given
        final String unparsedRequest = """
        HEAD /non_existent.html HTTP/1.1
        User-Agent: PostmanRuntime/7.22.0
        Accept: */*
        Cache-Control: no-cache
        Host: localhost
        Accept-Encoding: gzip, deflate, br
        Connection: keep-alive
        """;
        final Optional<Request> request = parser.parse(unparsedRequest);

        // when
        final Optional<Response> headResponse = executor.execute(request.get());

        // then
        assertTrue(headResponse.isEmpty());

    }
}
