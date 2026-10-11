package edu.byohttp.request;

import edu.byohttp.response.Response;

import java.util.Optional;

public interface RequestMethodExecutor {
    Optional<Response> execute(Request request);
}
