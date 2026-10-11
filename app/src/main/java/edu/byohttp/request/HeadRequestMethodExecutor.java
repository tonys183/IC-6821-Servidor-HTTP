package edu.byohttp.request;

import edu.byohttp.date.DateFormatter;
import edu.byohttp.resource.Resource;
import edu.byohttp.resource.ResourceFetcher;
import edu.byohttp.response.Response;
import edu.byohttp.response.mapper.HttpStatus;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

public final class HeadRequestMethodExecutor implements RequestMethodExecutor {
    private static final String SERVER_HEADER_KEY = "Server";
    private static final String SERVER_HEADER_VALUE = "byohttp/0.0.1";
    private static final String DATE_HEADER_KEY = "Date";
    private static final String CONTENT_TYPE_HEADER_KEY = "Content-Type";
    private static final String CONTENT_LENGTH_HEADER_KEY = "Content-Length";
    private static final String LAST_MODIFIED_HEADER_KEY = "Last-Modified";
    private static final String CONNECTION_HEADER_KEY = "Connection";
    private static final String CONNECTION_HEADER_VALUE = "keep-alive";
    private static final String ACCEPT_RANGES_HEADER_KEY = "Accept-Ranges";
    private static final String ACCEPT_RANGES_HEADER_VALUE = "bytes";

    private final ResourceFetcher fetcher;
    private final DateFormatter dateFormatter;

    public HeadRequestMethodExecutor(final ResourceFetcher resourceFetcher,
                                     final DateFormatter dateFormatter) {
        this.fetcher = resourceFetcher;
        this.dateFormatter = dateFormatter;
    }

    @Override
    public Optional<Response> execute(Request request) {
        final Optional<Resource> resource = this.fetcher.fetch(request.resourcePath());
        return resource.isEmpty()
                ? Optional.empty()
                : Optional.of(new Response(
                request.httpVersion(),
                HttpStatus.OK,
                createHeaders(resource.get()),
                Optional.empty()
        ));
    }

    private Map<String, String> createHeaders(Resource resource) {
        return Map.of(
                SERVER_HEADER_KEY, SERVER_HEADER_VALUE,
                DATE_HEADER_KEY, this.dateFormatter.format(new Date()),
                CONTENT_TYPE_HEADER_KEY, resource.mimeType(),
                CONTENT_LENGTH_HEADER_KEY, String.valueOf(resource.length()),
                LAST_MODIFIED_HEADER_KEY,  this.dateFormatter.format(resource.lastModified()),
                CONNECTION_HEADER_KEY, CONNECTION_HEADER_VALUE,
                ACCEPT_RANGES_HEADER_KEY, ACCEPT_RANGES_HEADER_VALUE
        );
    }
}
