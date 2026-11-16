package io.github.mary2820.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// the error body of dummyjson; the status is the real HTTP status, not a field
@JsonIgnoreProperties(ignoreUnknown = true)
public record ErrorResponse(String message) {
}
