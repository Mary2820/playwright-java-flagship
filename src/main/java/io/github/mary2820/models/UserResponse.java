package io.github.mary2820.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// user is absent when responseCode is not 200, and message is absent when it is
@JsonIgnoreProperties(ignoreUnknown = true)
public record UserResponse(int responseCode, String message, User user) {
}
