package edu.byohttp.response;

import edu.byohttp.date.DateFormatter;
import edu.byohttp.resource.Resource;
import edu.byohttp.resource.ResourceFetcher;
import edu.byohttp.response.mapper.HttpStatus;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

public final class NotImplementedResponseCreator implements ResponseCreator {
    private static final String SERVER_HEADER_KEY = "Server";
    private static final String SERVER_HEADER_VALUE = "byohttp/0.0.1";
    private static final String DATE_HEADER_KEY = "Date";
    private static final String CONTENT_TYPE_HEADER_KEY = "Content-Type";
    private static final String CONTENT_TYPE_HEADER_VALUE = "text/html";
    private static final String CONTENT_LENGTH_HEADER_KEY = "Content-Length";
    private static final String CONNECTION_HEADER_KEY = "Connection";
    private static final String CONNECTION_HEADER_VALUE = "close";
    private static final String ERROR_FILE_PATH = "501.html";

    private final ResourceFetcher resourceFetcher;
    private final DateFormatter dateFormatter;

    public NotImplementedResponseCreator(final ResourceFetcher resourceFetcher,
                                         final DateFormatter dateFormatter) {
        this.resourceFetcher = resourceFetcher;
        this.dateFormatter = dateFormatter;
    }

    @Override
    public Response create(String requestHttpVersion) {
        final Optional<Resource> resource = this.resourceFetcher.fetch(ERROR_FILE_PATH);
        return new Response(
                requestHttpVersion,
                HttpStatus.NOT_IMPLEMENTED,
                createHeaders(resource),
                resource
        );
    }

    private Map<String, String> createHeaders(Optional<Resource> errorResource) {
        return Map.of(
            SERVER_HEADER_KEY, SERVER_HEADER_VALUE,
            DATE_HEADER_KEY, this.dateFormatter.format(new Date()),
            CONTENT_TYPE_HEADER_KEY, CONTENT_TYPE_HEADER_VALUE,
            CONTENT_LENGTH_HEADER_KEY, String.valueOf(
                    errorResource.map(Resource::length).orElse(0L)),
            CONNECTION_HEADER_KEY, CONNECTION_HEADER_VALUE
        );
    }
}
