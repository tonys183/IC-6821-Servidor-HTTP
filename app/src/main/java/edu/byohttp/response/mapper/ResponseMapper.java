package edu.byohttp.response.mapper;

import edu.byohttp.response.ResponseCreator;

public interface ResponseMapper {
    ResponseCreator get(HttpStatus responseHttpStatus);
}
