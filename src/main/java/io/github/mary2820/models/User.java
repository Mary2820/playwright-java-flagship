package io.github.mary2820.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

// the user as /api/getUserDetailByEmail returns it, TestUser is what the tests send;
// the response has no password, and the optional fields come back as empty strings
@JsonIgnoreProperties(ignoreUnknown = true)
public record User(int id, String name, String email,
                   @JsonProperty("first_name") String firstName,
                   @JsonProperty("last_name") String lastName,
                   String address1, String country, String zipcode, String state, String city) {
}
