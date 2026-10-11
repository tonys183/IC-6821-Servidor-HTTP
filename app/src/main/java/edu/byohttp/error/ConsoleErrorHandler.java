package edu.byohttp.error;

import edu.byohttp.config.DependenciesContainer;
import edu.byohttp.response.Response;
import edu.byohttp.response.ServerErrorResponseCreator;

public final class ConsoleErrorHandler implements ErrorHandler {
    private static final String HTTP_VERSION = "HTTP/1.1";
    private final ServerErrorResponseCreator errorResponseCreator;

    public ConsoleErrorHandler(final ServerErrorResponseCreator errorResponseCreator) {
        this.errorResponseCreator = errorResponseCreator;
    }

    @Override
    public Response handle(Exception exception) {
        DependenciesContainer.log().error(exception);
        return errorResponseCreator.create(HTTP_VERSION);
    }
}
