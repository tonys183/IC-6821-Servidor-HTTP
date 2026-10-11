package edu.byohttp.response.mapper;

import edu.byohttp.response.ResponseCreator;
import java.util.Map;

public final class MapResponseMapper implements ResponseMapper {
    private final Map<HttpStatus, ResponseCreator> responses;

    public MapResponseMapper(final Map<HttpStatus, ResponseCreator> responses) {
        this.responses = responses;
    }

    @Override
    public ResponseCreator get(HttpStatus responseHttpStatus) {
        return this.responses.get(responseHttpStatus);
    }
}
