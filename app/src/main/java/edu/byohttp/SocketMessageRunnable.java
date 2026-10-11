package edu.byohttp;

import edu.byohttp.config.DependenciesContainer;
import edu.byohttp.resource.Resource;
import edu.byohttp.response.Response;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public final class SocketMessageRunnable implements Runnable {
    private final Socket socket;
    private static final String STATUS_LINE_FORMAT = "%s %s %s%n";
    private static final String HEADER_SEPARATOR = ": ";
    private static final int BUFFER_SIZE = 16384;
    private static final int NONE_BUFFER = -1;

    public SocketMessageRunnable(final Socket socket) throws IOException {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (Socket socket = this.socket;
             InputStream in = socket.getInputStream();
             OutputStream out = socket.getOutputStream()) {

            final String requestString = readHttpRequestHeaders(in);
            final Response response = DependenciesContainer.httpServerExecutor().execute(requestString);
            writeResponseOut(response, out);
            out.flush();

        } catch (IOException e) {
            DependenciesContainer.errorHandler().handle(e);
        }
    }

    private void writeResponseOut(Response response, OutputStream out) throws IOException  {
        out.write(STATUS_LINE_FORMAT
                .formatted(
                        response.httpVersion(),
                        response.httpStatus().code(),
                        response.httpStatus().reason()
                ).getBytes(StandardCharsets.UTF_8));

        for (Map.Entry<String, String> header : response.headers().entrySet()) {
            out.write((header.getKey()
                    + HEADER_SEPARATOR + header.getValue() + System.lineSeparator())
                    .getBytes(StandardCharsets.UTF_8));
        }

        out.write(System.lineSeparator().getBytes(StandardCharsets.UTF_8));

        if (response.resource().isPresent()) {
            final Resource resource = response.resource().get();
            try (InputStream body = resource.body()) {
                final byte[] buffer = new byte[BUFFER_SIZE];
                int bytesRead;
                while ((bytesRead = body.read(buffer)) != NONE_BUFFER) {
                    out.write(buffer, 0, bytesRead);
                }
            }
        }
    }

    private String readHttpRequestHeaders(InputStream in) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        final StringBuilder requestBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            requestBuilder.append(line).append(System.lineSeparator());
        }
        return requestBuilder.toString();
    }
}
