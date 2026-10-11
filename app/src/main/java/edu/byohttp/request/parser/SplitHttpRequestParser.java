package edu.byohttp.request.parser;

import edu.byohttp.request.Request;
import edu.byohttp.request.mapper.RequestMethod;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public final class SplitHttpRequestParser implements HttpRequestParser {
    private static final String LINES_SEPARATOR = "\\r?\\n";
    private static final char HEADER_SEPARATOR = ':';
    private static final int METHOD_INDEX = 0;
    private static final int RESOURCE_INDEX = 1;
    private static final int HTTP_INDEX = 2;
    private static final int FIRST_LINE = 1;
    private static final String NEXT_LINE = "\n";

    @Override
    public Optional<Request> parse(String request) {
        final String firstLine = request.lines().findFirst().orElse("");
        final String[] parsedLine = parseFirstLine(firstLine);
        final String header = request.lines()
                .skip(FIRST_LINE)
                .collect(Collectors.joining(NEXT_LINE));

        if (header.isBlank()) {
            return Optional.empty();
        }

        final Optional<Map<String, String>> mappedHeader = parseHeader(header);

        return Optional.of(new Request(
            RequestMethod.fromString(parsedLine[METHOD_INDEX]),
            parsedLine[RESOURCE_INDEX],
            parsedLine[HTTP_INDEX],
            mappedHeader
        ));
    }

    private String[] parseFirstLine(String firstLine) {
        return firstLine.split(" ");
    }

    private Optional<Map<String, String>> parseHeader(String header) {
        final String[] headerLines = header.split(LINES_SEPARATOR);
        final Map<String, String> parsedHeader = new HashMap<>();
        int i = 0;
        boolean isValid = true;
        while (i < headerLines.length && isValid) {
            final String headerLine = headerLines[i].trim();

            if (!headerLine.isEmpty()) {
                int separator = headerLine.indexOf(HEADER_SEPARATOR);
                if (separator > 0) {
                    final String key = headerLine.substring(0, separator).trim();
                    final String value = headerLine.substring(++separator).trim();

                    if (!key.isEmpty() && !value.isEmpty()) {
                        parsedHeader.put(key, value);
                    } else {
                        isValid = false;
                    }
                } else {
                    isValid = false;
                }
            }
            i++;
        }
        return isValid ? Optional.of(parsedHeader) : Optional.empty();
    }
}
