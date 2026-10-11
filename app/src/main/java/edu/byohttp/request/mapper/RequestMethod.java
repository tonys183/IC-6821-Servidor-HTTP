package edu.byohttp.request.mapper;

import java.util.Optional;

public enum RequestMethod {
    CONNECT("CONNECT"),
    DELETE("DELETE"),
    GET("GET"),
    HEAD("HEAD"),
    OPTIONS("OPTIONS"),
    PATCH("PATCH"),
    POST("POST"),
    PUT("PUT"),
    TRACE("TRACE");
    private final String name;

    RequestMethod(final String name) {
        this.name = name;
    }

    public static Optional<RequestMethod> fromString(String text) {
        for (RequestMethod method : RequestMethod.values()) {
            if (method.name.equals(text)) {
                return Optional.of(method);
            }
        }
        return Optional.empty();
    }
}
