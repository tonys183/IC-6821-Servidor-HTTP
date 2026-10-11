package edu.byohttp.request.mapper;

import edu.byohttp.request.RequestMethodExecutor;
import java.util.Map;
import java.util.Optional;

public final class MapRequestMapper implements RequestMapper {
    private final Map<RequestMethod, RequestMethodExecutor> requests;

    public MapRequestMapper(final Map<RequestMethod, RequestMethodExecutor> requests) {
        this.requests = requests;
    }

    @Override
    public Optional<RequestMethodExecutor> get(RequestMethod request) {
        return Optional.ofNullable(this.requests.get(request));
    }

}
