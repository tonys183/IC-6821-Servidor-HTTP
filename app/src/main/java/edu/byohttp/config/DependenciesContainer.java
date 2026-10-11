package edu.byohttp.config;

import edu.byohttp.error.ErrorHandler;
import edu.byohttp.log.Log;
import edu.byohttp.server.HttpServerExecutor;
import java.util.Objects;

public final class DependenciesContainer {
    private static volatile HttpServerExecutor httpServerExecutor;
    private static volatile ErrorHandler errorHandler;
    private static volatile Log log;

    private DependenciesContainer() {

    }

    public static void configure(HttpServerExecutor executor, ErrorHandler handler, Log logger) {
        httpServerExecutor = Objects.requireNonNull(executor, "requestExecutor");
        errorHandler = Objects.requireNonNull(handler, "errorHandler");
        log = Objects.requireNonNull(logger, "logger");
    }

    public static HttpServerExecutor httpServerExecutor() {
        return httpServerExecutor;
    }

    public static ErrorHandler errorHandler() {
        return errorHandler;
    }

    public static Log log() {
        return log;
    }

}
