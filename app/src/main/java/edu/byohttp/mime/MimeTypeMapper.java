package edu.byohttp.mime;

import java.util.Optional;

public interface MimeTypeMapper {
    void map(String path);
    Optional<String> get(String extension);
}
