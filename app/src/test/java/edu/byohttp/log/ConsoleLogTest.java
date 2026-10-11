package edu.byohttp.log;

import edu.byohttp.date.DateFormatter;
import edu.byohttp.date.IsoDateFormatter;
import edu.byohttp.request.Request;
import edu.byohttp.request.mapper.RequestMethod;
import edu.byohttp.response.Response;
import edu.byohttp.response.mapper.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Map;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class ConsoleLogTest {
    private static final String MESSAGE_INFO = "DUMMY MESSAGE";
    private static final String MESSAGE_ERROR = "ERROR MESSAGE";
    private static final String PREFIX_INFO = "[INFO]";
    private static final String PREFIX_ERROR = "[ERROR]";
    private static final String PREFIX_REQUEST = "[REQUEST]";
    private static final String PREFIX_RESPONSE = "[RESPONSE]";
    private static final String HTTP_VERSION = "HTTP/1.1";
    private static final String RESOURCE_PATH = "/helloworld.html";
    private static final String HEADER_USER_AGENT_KEY = "User-Agent";
    private static final String HEADER_USER_AGENT_VALUE = "PostmanRuntime/7.22.0";
    private static final String HEADER_ACCEPT_KEY = "Accept";
    private static final String HEADER_ACCEPT_VALUE = "*/*";
    private static final String HEADER_CACHE_CONTROL_KEY = "Cache-Control";
    private static final String HEADER_CACHE_CONTROL_VALUE = "no-cache";
    private static final String HEADER_HOST_KEY = "Host";
    private static final String HEADER_HOST_VALUE = "localhost";
    private static final String HEADER_ACCEPT_ENCODING_KEY = "Accept-Encoding";
    private static final String HEADER_ACCEPT_ENCODING_VALUE = "gzip, deflate, br";
    private static final String HEADER_CONNECTION_KEY = "Connection";
    private static final String HEADER_CONNECTION_VALUE = "keep-alive";
    private static final String HEADER_SEPARATOR = ": ";

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private static DateFormatter dateFormatter;
    private static ConsoleLog log;

    @BeforeEach
    void setup() {
        dateFormatter = new IsoDateFormatter();
        log = new ConsoleLog(dateFormatter);
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    void teardown() {
        System.setOut(originalOut);
        outputStream.reset();
    }

    @Test
    void infoTest() {
        // given
        
        // when
        log.info(MESSAGE_INFO);
        final String output = outputStream.toString();
        System.setOut(originalOut);
        System.out.println(output);

        // then
        assertTrue(output.contains(PREFIX_INFO));
        assertTrue(output.contains(MESSAGE_INFO));
    }

    @Test
    void errorTest() {
        // given
        System.setErr(new PrintStream(outputStream));
        final Exception exception = new NullPointerException(MESSAGE_ERROR);

        // when
        log.error(exception);
        final String output = outputStream.toString();

        // then
        assertTrue(output.contains(PREFIX_ERROR));
        assertTrue(output.contains(NullPointerException.class.getName()));
        assertTrue(output.contains(MESSAGE_ERROR));

        System.setErr(System.err);
        outputStream.reset();
    }

    @Test
    void requestTest() {
        // given
        final Map<String, String> headers = Map.of(
                HEADER_USER_AGENT_KEY, HEADER_USER_AGENT_VALUE,
                HEADER_ACCEPT_KEY, HEADER_ACCEPT_VALUE,
                HEADER_CACHE_CONTROL_KEY, HEADER_CACHE_CONTROL_VALUE,
                HEADER_HOST_KEY, HEADER_HOST_VALUE,
                HEADER_ACCEPT_ENCODING_KEY, HEADER_ACCEPT_ENCODING_VALUE,
                HEADER_CONNECTION_KEY, HEADER_CONNECTION_VALUE
        );

        final Request request = new Request(
                Optional.of(RequestMethod.GET),
                RESOURCE_PATH,
                HTTP_VERSION,
                Optional.of(headers)
        );

        // when
        log.request(request);
        final String output = outputStream.toString();

        // then
        assertTrue(output.contains(PREFIX_REQUEST));
        assertTrue(output.contains(RequestMethod.GET.name()));
        assertTrue(output.contains(RESOURCE_PATH + " "));
        assertTrue(output.contains(HEADER_HOST_KEY + HEADER_SEPARATOR + HEADER_HOST_VALUE));
    }

    @Test
    void responseTest() {
        // given
        final Map<String, String> headers = Map.of(
                HEADER_USER_AGENT_KEY, HEADER_USER_AGENT_VALUE,
                HEADER_ACCEPT_KEY, HEADER_ACCEPT_VALUE,
                HEADER_CACHE_CONTROL_KEY, HEADER_CACHE_CONTROL_VALUE,
                HEADER_HOST_KEY, HEADER_HOST_VALUE,
                HEADER_ACCEPT_ENCODING_KEY, HEADER_ACCEPT_ENCODING_VALUE,
                HEADER_CONNECTION_KEY, HEADER_CONNECTION_VALUE
        );

        final Response response = new Response(
                HTTP_VERSION,
                HttpStatus.OK,
                headers,
                Optional.empty()
        );

        // when
        log.response(response);
        final String output = outputStream.toString();
        System.setOut(originalOut);
        System.out.println(output);

        // then
        assertTrue(output.contains(PREFIX_RESPONSE));
        assertTrue(output.contains(HttpStatus.OK.toString()));
        assertTrue(output.contains(HEADER_CONNECTION_KEY + HEADER_SEPARATOR + HEADER_CONNECTION_VALUE));
    }
}
