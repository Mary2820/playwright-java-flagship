package io.github.mary2820.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record LoginResponse(String accessToken, String refreshToken, int id, String username, String email) {
}
