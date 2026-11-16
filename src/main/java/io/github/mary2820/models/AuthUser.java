package io.github.mary2820.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// the fields of dummyjson GET /auth/me that the tests compare
@JsonIgnoreProperties(ignoreUnknown = true)
public record AuthUser(int id, String username, String email) {
}
