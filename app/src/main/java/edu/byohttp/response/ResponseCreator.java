package edu.byohttp.response;

public interface ResponseCreator {
    Response create(String requestHttpVersion);
}
