package edu.byohttp.request;

import edu.byohttp.request.mapper.RequestMethod;
import java.util.Map;
import java.util.Optional;

public record Request(
    Optional<RequestMethod> method,
    String resourcePath,
    String httpVersion,
    Optional<Map<String, String>> headers
) { }
