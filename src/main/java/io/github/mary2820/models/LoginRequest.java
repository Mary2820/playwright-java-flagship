package io.github.mary2820.models;

// the JSON body of dummyjson POST /auth/login
public record LoginRequest(String username, String password) {
}
