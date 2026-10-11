package edu.byohttp.server;

import edu.byohttp.config.DependenciesContainer;
import edu.byohttp.request.Request;
import edu.byohttp.request.RequestMethodExecutor;
import edu.byohttp.request.mapper.RequestMapper;
import edu.byohttp.request.parser.HttpRequestParser;
import edu.byohttp.response.Response;
import edu.byohttp.response.mapper.HttpStatus;
import edu.byohttp.response.mapper.ResponseMapper;
import java.util.Optional;

public final class MappedHttpServerExecutor implements HttpServerExecutor {
    private static final String HTTP_VERSION = "HTTP/1.1";
    private final HttpRequestParser requestParser;
    private final RequestMapper requestMapper;
    private final ResponseMapper responseMapper;

    public MappedHttpServerExecutor(final HttpRequestParser requestParser,
                                    final RequestMapper requestMapper,
                                    final ResponseMapper responseMapper) {
        this.requestParser = requestParser;
        this.requestMapper = requestMapper;
        this.responseMapper = responseMapper;
    }

    @Override
    public Response execute(String request) {
        final Optional<Response> response;
        try {
            final Optional<Request> httpRequest = this.requestParser.parse(request);

            if (httpRequest.isEmpty()
                || httpRequest.get().method().isEmpty()
                || httpRequest.get().headers().isEmpty()) {
                return errorResponse(HttpStatus.BAD_REQUEST);
            }
            DependenciesContainer.log().request(httpRequest.get());

            final Optional<RequestMethodExecutor> methodExecutor =
                    this.requestMapper.get(httpRequest.get().method().get());

            if (methodExecutor.isEmpty()) {
                return errorResponse(HttpStatus.NOT_IMPLEMENTED);
            }

            response = methodExecutor.get().execute(httpRequest.get());
            if (response.isEmpty()) {
                return errorResponse(HttpStatus.NOT_FOUND);
            }

        } catch (Exception e) {
            return DependenciesContainer.errorHandler().handle(e);
        }
        DependenciesContainer.log().response(response.get());
        return response.get();
    }

    private Response errorResponse(HttpStatus httpStatus) {
        final Response errorResponse = this.responseMapper.get(httpStatus).create(HTTP_VERSION);
        DependenciesContainer.log().response(errorResponse);
        return errorResponse;
    }
}
