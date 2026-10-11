package edu.byohttp.log;

import edu.byohttp.request.Request;
import edu.byohttp.response.Response;

public interface Log {
    void info(String message);
    void request(Request request);
    void response(Response response);
    void error(Exception e);
}
