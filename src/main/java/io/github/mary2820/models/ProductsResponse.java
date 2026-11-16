package io.github.mary2820.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ProductsResponse(int responseCode, List<Product> products) {
}
