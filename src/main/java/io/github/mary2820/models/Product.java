package io.github.mary2820.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// the fields of one entry of /api/productsList that the tests compare with the UI
@JsonIgnoreProperties(ignoreUnknown = true)
public record Product(int id, String name, String price, String brand, Category category) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Category(UserType usertype, String category) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record UserType(String usertype) {
    }

    // the product page shows it as "Women > Tops"
    public String categoryLabel() {
        return category.usertype().usertype() + " > " + category.category();
    }
}
