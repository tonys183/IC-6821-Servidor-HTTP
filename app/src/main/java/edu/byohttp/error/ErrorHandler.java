package edu.byohttp.error;

import edu.byohttp.response.Response;

public interface ErrorHandler {
    Response handle(Exception exception);
}
