package edu.byohttp.request.parser;

import edu.byohttp.request.Request;
import edu.byohttp.request.mapper.RequestMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Map;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class SplitHttpRequestParserTest {
    private static HttpRequestParser splitHttpRequestParser;

    @BeforeEach
    public void setup() {
        splitHttpRequestParser = new SplitHttpRequestParser();
    }

    @Test
    public void parseGoodTest() {
        // given
        final String getRequest = """
        GET /helloworld.html HTTP/1.1
        User-Agent: PostmanRuntime/7.22.0
        Accept: */*
        Cache-Control: no-cache
        Host: localhost
        Accept-Encoding: gzip, deflate, br
        Connection: keep-alive
        """;

        final Map<String, String> headers = Map.of(
            "User-Agent", "PostmanRuntime/7.22.0",
            "Accept", "*/*",
            "Cache-Control", "no-cache",
            "Host", "localhost",
            "Accept-Encoding", "gzip, deflate, br",
            "Connection", "keep-alive"
        );

        final Request goodRequest = new Request(
                Optional.of(RequestMethod.GET),
                "/helloworld.html",
                "HTTP/1.1",
                Optional.of(headers)
        );

        // when
        final Optional<Request> request = splitHttpRequestParser.parse(getRequest);

        // then
        assertTrue(request.isPresent());
        assertEquals(goodRequest, request.get());
    }

    @Test
    public void parseBadRequest() {
        // given
        final String getRequest = """
        CONNECT /helloworld.html HTTP/1.1
        User-Agent: PostmanRuntime/7.22.0
        Accept: */*
        Cache-Control: no-cache
        Host: localhost
        Accept-Encoding: gzip, deflate, br
        Connection: keep-alive
        """;

        // when
        final Optional<Request> request = splitHttpRequestParser.parse(getRequest);

        // then
        assertTrue(request.isPresent());
        assertTrue(request.get().method().isPresent());
        assertEquals(RequestMethod.CONNECT, request.get().method().get());
    }

    @Test
    public void parseBadHeadersRequest() {
        // given
        final String getRequest = """
        GET /helloworld.html HTTP/1.1
        User-Agent: PostmanRuntime/7.22.0
        Accept */*
        Cache-Control: no-cache
        Host: localhost
        Accept-Encoding: gzip, deflate, br
        Connection: keep-alive
        """;

        // when
        final Optional<Request> request = splitHttpRequestParser.parse(getRequest);

        // then
        assertTrue(request.isPresent());
        assertTrue(request.get().headers().isEmpty());
    }
}
