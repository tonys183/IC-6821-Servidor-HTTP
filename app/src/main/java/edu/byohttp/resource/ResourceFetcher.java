package edu.byohttp.resource;

import java.util.Optional;

public interface ResourceFetcher {
    Optional<Resource> fetch(String resource);
}
