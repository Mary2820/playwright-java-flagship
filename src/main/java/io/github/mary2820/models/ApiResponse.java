package io.github.mary2820.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// the API answers HTTP 200 even on errors, the real status is responseCode in the body
@JsonIgnoreProperties(ignoreUnknown = true)
public record ApiResponse(int responseCode, String message) {
}