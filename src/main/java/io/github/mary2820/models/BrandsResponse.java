package io.github.mary2820.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BrandsResponse(int responseCode, List<Brand> brands) {
}
