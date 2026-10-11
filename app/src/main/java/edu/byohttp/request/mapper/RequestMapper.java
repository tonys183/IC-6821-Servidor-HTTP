package edu.byohttp.request.mapper;

import edu.byohttp.request.RequestMethodExecutor;
import java.util.Optional;

public interface RequestMapper {
    Optional<RequestMethodExecutor> get(RequestMethod request);
}
