package edu.byohttp.resource;

import java.io.InputStream;
import java.util.Date;

public record Resource(
    String path,
    String mimeType,
    long length,
    Date lastModified,
    InputStream body
) { }
