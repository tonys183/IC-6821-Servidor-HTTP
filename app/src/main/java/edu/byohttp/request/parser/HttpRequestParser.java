package edu.byohttp.request.parser;

import edu.byohttp.request.Request;
import java.util.Optional;

public interface HttpRequestParser {
    Optional<Request> parse(String request);
}
