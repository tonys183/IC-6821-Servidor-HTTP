package edu.byohttp.log;

import edu.byohttp.date.DateFormatter;
import edu.byohttp.request.Request;
import edu.byohttp.response.Response;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class ConsoleLog implements Log {
    private static final String INFO_FORMAT = "[INFO] %s%n";
    private static final String ERROR_FORMAT = "[ERROR] %s %s: %s%n%s%n";
    private static final String REQUEST_FORMAT = "[REQUEST] %s%n%s %s %s%n%s%n";
    private static final String RESPONSE_FORMAT = "[RESPONSE] %s%n%s %s%n%s%n";
    private static final String HEADER_SEPARATOR = ": ";
    private static final String STACK_TRACE_PREFIX = "\tat ";
    private static final List<String> RESPONSE_HEADER_ORDER = Arrays.asList(
            "Server", "Date", "Content-Type", "Content-Length",
            "Last-Modified", "Connection", "Accept-Ranges"
    );
    private static final List<String> REQUEST_HEADER_ORDER = Arrays.asList(
            "User-Agent", "Accept", "Cache-Control", "Host",
            "Accept-Encoding", "Connection"
    );

    private final DateFormatter dateFormatter;

    public ConsoleLog(final DateFormatter dateFormatter) {
        this.dateFormatter = dateFormatter;
    }

    @Override
    public void info(String message) {
        System.out.println(INFO_FORMAT.formatted(message));
    }

    @Override
    public void request(Request request) {
        System.out.println(REQUEST_FORMAT.formatted(
                this.dateFormatter.format(new Date()),
                request.method().get(), request.resourcePath(), request.httpVersion(),
                formatHeaders(request.headers().get(), REQUEST_HEADER_ORDER)
        ));
    }

    @Override
    public void response(Response response) {
        System.out.println(RESPONSE_FORMAT.formatted(
                this.dateFormatter.format(new Date()),
                response.httpVersion(), response.httpStatus(),
                formatHeaders(response.headers(), RESPONSE_HEADER_ORDER)
        ));
    }

    @Override
    public void error(Exception exception) {
        final String stackTrace = Arrays.stream(exception.getStackTrace())
                .map(ste -> STACK_TRACE_PREFIX  +  ste)
                .collect(Collectors.joining(System.lineSeparator()));

        System.err.print(ERROR_FORMAT.formatted(
                this.dateFormatter.format(new Date()),
                exception.getClass().getName(),
                exception.getMessage(),
                stackTrace
        ));
    }

    private String formatHeaders(Map<String, String> headers, List<String> order) {
        return order.stream()
                .filter(headers::containsKey)
                .map(header -> header + HEADER_SEPARATOR + headers.get(header))
                .collect(Collectors.joining(System.lineSeparator()));
    }
}
