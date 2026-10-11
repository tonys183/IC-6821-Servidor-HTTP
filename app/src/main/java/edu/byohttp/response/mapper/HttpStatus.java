package edu.byohttp.response.mapper;

public enum HttpStatus {
    OK(200, "OK"),
    BAD_REQUEST(400, "Bad Request"),
    NOT_FOUND(404, "Not Found"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    NOT_IMPLEMENTED(501, "Not Implemented");

    private final int code;
    private final String reason;

    HttpStatus(final int code, final String reason) {
        this.code = code;
        this.reason = reason;
    }

    public int code() {
        return this.code;
    }

    public String reason() {
        return this.reason;
    }

    @Override
    public String toString() {
        return this.code + " " + this.reason;
    }
}
