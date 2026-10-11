package edu.byohttp.config;

import edu.byohttp.date.DateFormatter;
import edu.byohttp.date.HttpDateFormatter;
import edu.byohttp.date.IsoDateFormatter;
import edu.byohttp.error.ConsoleErrorHandler;
import edu.byohttp.error.ErrorHandler;
import edu.byohttp.log.ConsoleLog;
import edu.byohttp.log.Log;
import edu.byohttp.mime.CsvMimeTypeMapper;
import edu.byohttp.mime.MimeTypeMapper;
import edu.byohttp.request.GetRequestMethodExecutor;
import edu.byohttp.request.HeadRequestMethodExecutor;
import edu.byohttp.request.RequestMethodExecutor;
import edu.byohttp.request.mapper.MapRequestMapper;
import edu.byohttp.request.mapper.RequestMapper;
import edu.byohttp.request.mapper.RequestMethod;
import edu.byohttp.request.parser.HttpRequestParser;
import edu.byohttp.request.parser.SplitHttpRequestParser;
import edu.byohttp.resource.ClasspathResourceFetcher;
import edu.byohttp.resource.DirectoryResourceFetcher;
import edu.byohttp.resource.ResourceFetcher;
import edu.byohttp.response.BadRequestResponseCreator;
import edu.byohttp.response.NotFoundResponseCreator;
import edu.byohttp.response.NotImplementedResponseCreator;
import edu.byohttp.response.ServerErrorResponseCreator;
import edu.byohttp.response.ResponseCreator;
import edu.byohttp.response.mapper.HttpStatus;
import edu.byohttp.response.mapper.MapResponseMapper;
import edu.byohttp.response.mapper.ResponseMapper;
import edu.byohttp.server.HttpServerExecutor;
import edu.byohttp.server.MappedHttpServerExecutor;
import java.io.File;
import java.nio.file.Path;
import java.util.Map;

public final class DefaultConfig implements Config {
    private final File resourcesDirectory;
    private final File mimeTypesMapping;

    public DefaultConfig(final File resourcesDirectory,
                         final File mimeTypesMapping) {
        this.resourcesDirectory = resourcesDirectory;
        this.mimeTypesMapping = mimeTypesMapping;
    }

    @Override
    public void intialize() {
        final MimeTypeMapper mimeTypeMapper = new CsvMimeTypeMapper();
        mimeTypeMapper.map(this.mimeTypesMapping.getPath());

        final DateFormatter httpDateFormatter = new HttpDateFormatter();

        final DateFormatter isoDateFormatter = new IsoDateFormatter();

        final Log logger = new ConsoleLog(isoDateFormatter);

        final ResourceFetcher directoryResourceFetcher =
                new DirectoryResourceFetcher(Path.of(
                        this.resourcesDirectory.getPath()), mimeTypeMapper);

        final ResourceFetcher classpathResourceFetcher = new ClasspathResourceFetcher();

        final ResponseMapper mapResponseMapper = getResponseMapper(classpathResourceFetcher, httpDateFormatter);

        final RequestMapper mapRequestMapper = getRequestMapper(directoryResourceFetcher, httpDateFormatter);

        final HttpRequestParser httpRequestParser = new SplitHttpRequestParser();

        final HttpServerExecutor serverExecutor = new MappedHttpServerExecutor(
                httpRequestParser,
                mapRequestMapper,
                mapResponseMapper
        );

        final ErrorHandler consoleErrorHandler = new ConsoleErrorHandler(
                new ServerErrorResponseCreator(
                    classpathResourceFetcher,
                    httpDateFormatter
                )
        );

        DependenciesContainer.configure(serverExecutor, consoleErrorHandler, logger);
    }

    private static RequestMapper getRequestMapper(
            final ResourceFetcher directoryResourceFetcher,
            final DateFormatter httpDateFormatter) {

        final Map<RequestMethod, RequestMethodExecutor> requestMap = Map.of(
                RequestMethod.GET, new GetRequestMethodExecutor(
                        directoryResourceFetcher,
                        httpDateFormatter),
                RequestMethod.HEAD, new HeadRequestMethodExecutor(
                        directoryResourceFetcher,
                        httpDateFormatter
                )
        );

        return new MapRequestMapper(requestMap);
    }

    private static ResponseMapper getResponseMapper(
                                                    final ResourceFetcher classpathResourceFetcher,
                                                    final DateFormatter httpDateFormatter) {
        final Map<HttpStatus, ResponseCreator> responseMap = Map.of(
                HttpStatus.BAD_REQUEST, new BadRequestResponseCreator(
                        classpathResourceFetcher,
                        httpDateFormatter
                ),
                HttpStatus.NOT_FOUND, new NotFoundResponseCreator(
                        classpathResourceFetcher,
                        httpDateFormatter
                ),
                HttpStatus.NOT_IMPLEMENTED, new NotImplementedResponseCreator(
                        classpathResourceFetcher,
                        httpDateFormatter
                )
        );
        return new MapResponseMapper(responseMap);
    }
}
