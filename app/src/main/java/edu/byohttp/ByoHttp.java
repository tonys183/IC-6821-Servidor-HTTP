package edu.byohttp;

import edu.byohttp.config.Config;
import edu.byohttp.config.DefaultConfig;
import edu.byohttp.config.DependenciesContainer;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Optional;
import java.util.OptionalInt;

public final class ByoHttp {
    private static final int ARGS_COUNT = 3;

    private final int port;
    private final File resourcesDirectory;
    private final File mimeTypesMapping;
    private static final String STARTUP_FORMAT =
            "byohttp server startup successful, listening on port %s%n"
            + "Mime type mapping file: %s%n"
            + "Resource directory: %s";

    ByoHttp(final int port, final File resourcesDirectory, final File mimeTypesMapping) {
        this.port = port;
        this.resourcesDirectory = resourcesDirectory;
        this.mimeTypesMapping = mimeTypesMapping;
    }

    void run() {
        final Config config = new DefaultConfig(resourcesDirectory, mimeTypesMapping);
        try (ServerSocket serverSocket = new ServerSocket(this.port)) {
            config.intialize();
            DependenciesContainer.log().info(STARTUP_FORMAT.formatted(
                    this.port,
                    this.mimeTypesMapping.getPath(),
                    this.resourcesDirectory.getPath()
                    )
            );
            while (true) {
                new Thread(new SocketMessageRunnable(serverSocket.accept())).start();
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + this.port);
            System.exit(-1);
        }
    }

    public static void main(String... args) {
        if (!validateArgsCount(args)) {
            System.exit(1);
        }

        final OptionalInt port = validatePort(args[0]);
        if (port.isEmpty()) {
            System.exit(1);
        }

        final Optional<File> resourcePath = validateResourcePath(args[1]);
        if (resourcePath.isEmpty()) {
            System.exit(1);
        }

        final Optional<File> mimeTypeMapping = validateMimeTypeMapping(args[2]);
        if (mimeTypeMapping.isEmpty()) {
            System.exit(1);
        }
        final ByoHttp app = new ByoHttp(port.getAsInt(), resourcePath.get(), mimeTypeMapping.get());
        app.run();
    }

    private static Optional<File> validateMimeTypeMapping(String mimeTypeMappingArg) {
        final File mimeTypeMapping = new File(mimeTypeMappingArg);
        if (!mimeTypeMapping.exists() || !mimeTypeMapping.isFile()) {
            System.err.println("Argument <mime type mapping> should point to a file");
            return Optional.empty();
        }
        return Optional.of(mimeTypeMapping);
    }

    private static Optional<File> validateResourcePath(String resourcePathArg) {
        final File resourcePath = new File(resourcePathArg);
        if (!resourcePath.exists() || !resourcePath.isDirectory()) {
            System.err.println("Argument <resources path> should point to a directory");
            return Optional.empty();
        }
        return Optional.of(resourcePath);
    }

    private static OptionalInt validatePort(String portArg) {
        try {
            final int port = Integer.parseUnsignedInt(portArg);
            return OptionalInt.of(port);
        } catch (NumberFormatException nfe) {
            System.err.println("Argument <port number> should be a number");
            return OptionalInt.empty();
        }
    }

    private static boolean validateArgsCount(String[] args) {
        if (args.length != ARGS_COUNT) {
            printUsage();
            System.err.println("Invalid number of arguments");
            return false;
        }
        return true;
    }

    private static void printUsage() {
        System.out.println("Usage: byohttp <port number> <resources path> <mime type mapping>");
    }
}
