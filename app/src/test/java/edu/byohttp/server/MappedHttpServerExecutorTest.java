package edu.byohttp.server;

import edu.byohttp.config.Config;
import edu.byohttp.config.DefaultConfig;
import edu.byohttp.config.DependenciesContainer;
import edu.byohttp.response.Response;
import edu.byohttp.response.mapper.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.File;
import java.nio.file.Path;

public final class MappedHttpServerExecutorTest {
    private static final String HTTP_VERSION = "HTTP/1.1";
    private static final Path MAPPING_DIRECTORY = Path.of("..", "mappings", "mimetype.csv");
    private static final Path LOCAL_DIRECTORY = Path.of("..", "www");
    private static final Config CONFIG = new DefaultConfig(new File(LOCAL_DIRECTORY.toString()),
            new File(MAPPING_DIRECTORY.toString()));;

    @BeforeEach
    void setup() {
        CONFIG.intialize();
    }

    @Test
    void executeOkGetResponseTest() {
        // given
        final String unparsedRequest = """
        GET /helloworld.html HTTP/1.1
        User-Agent: PostmanRuntime/7.22.0
        Accept: */*
        Cache-Control: no-cache
        Host: localhost
        Accept-Encoding: gzip, deflate, br
        Connection: keep-alive
        """;

        // when
        final Response response = DependenciesContainer.httpServerExecutor().execute(unparsedRequest);

        // then
        assertEquals(HttpStatus.OK, response.httpStatus());
        assertEquals(HTTP_VERSION, response.httpVersion());
        assertTrue(response.resource().isPresent());

    }

    @Test
    void executeOkHeadResponseTest() {
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

        // when
        final Response response = DependenciesContainer.httpServerExecutor().execute(unparsedRequest);

        // then
        assertEquals(HttpStatus.OK, response.httpStatus());
        assertEquals(HTTP_VERSION, response.httpVersion());
        assertTrue(response.resource().isEmpty());

    }

    @Test
    void executeNotImplementedResponseTest() {
        // given
        final String unparsedRequest = """
        TRACE /helloworld.html HTTP/1.1
        User-Agent: PostmanRuntime/7.22.0
        Accept: */*
        Cache-Control: no-cache
        Host: localhost
        Accept-Encoding: gzip, deflate, br
        Connection: keep-alive
        """;

        // when
        final Response response = DependenciesContainer.httpServerExecutor().execute(unparsedRequest);

        // then
        assertEquals(HttpStatus.NOT_IMPLEMENTED, response.httpStatus());
        assertEquals(HTTP_VERSION, response.httpVersion());
        assertTrue(response.resource().isPresent());

    }

    @Test
    void executeBadRequestResponseTest() {
        // given
        final String badRequest = "bad_request";

        // when
        final Response response = DependenciesContainer.httpServerExecutor().execute(badRequest);

        // then
        assertEquals(HttpStatus.BAD_REQUEST, response.httpStatus());
        assertEquals(HTTP_VERSION, response.httpVersion());
        assertTrue(response.resource().isPresent());
    }

    @Test
    void executeNotFoundResponseTest() {
        // given
        final String unparsedRequest = """
        GET /nonexistent.html HTTP/1.1
        User-Agent: PostmanRuntime/7.22.0
        Accept: */*
        Cache-Control: no-cache
        Host: localhost
        Accept-Encoding: gzip, deflate, br
        Connection: keep-alive
        """;

        // when
        final Response response = DependenciesContainer.httpServerExecutor().execute(unparsedRequest);

        // then
        assertEquals(HttpStatus.NOT_FOUND, response.httpStatus());
        assertEquals(HTTP_VERSION, response.httpVersion());
        assertTrue(response.resource().isPresent());
    }

    @Test
    void executeInternalServerErrorResponseTest() {
        // given
        final String error = null;

        // when
        final Response response = DependenciesContainer.httpServerExecutor().execute(error);

        // then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.httpStatus());
        assertEquals(HTTP_VERSION, response.httpVersion());
        assertTrue(response.resource().isPresent());
    }
}
