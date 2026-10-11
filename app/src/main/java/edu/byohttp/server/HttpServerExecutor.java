package edu.byohttp.server;

import edu.byohttp.response.Response;

public interface HttpServerExecutor {
    Response execute(String request);
}
