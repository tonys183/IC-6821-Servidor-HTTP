package edu.byohttp.response;

import edu.byohttp.resource.Resource;
import edu.byohttp.response.mapper.HttpStatus;
import java.util.Map;
import java.util.Optional;

public record Response(
    String httpVersion,
    HttpStatus httpStatus,
    Map<String, String> headers,
    Optional<Resource> resource
) { }
