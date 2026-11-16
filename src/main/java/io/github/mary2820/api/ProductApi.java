package io.github.mary2820.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import io.github.mary2820.models.Product;
import io.github.mary2820.models.ProductsResponse;

import java.util.List;

import static io.github.mary2820.constants.IConstants.OK;
import static io.github.mary2820.constants.IConstants.PRODUCTS_LIST_ENDPOINT;

public class ProductApi {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final APIRequestContext request;

    public ProductApi(APIRequestContext request) {
        this.request = request;
    }

    // fails fast: an empty or broken list would make the comparison with the UI meaningless
    public List<Product> list() {
        APIResponse response = request.get(PRODUCTS_LIST_ENDPOINT);
        try {
            ProductsResponse body = MAPPER.readValue(response.text(), ProductsResponse.class);
            if (body.responseCode() != OK || body.products() == null || body.products().isEmpty())
                throw new IllegalStateException("Setup failed: " + response.status() + " " + response.text());
            return body.products();
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Not a JSON response: " + response.status() + " " + response.text(), e);
        }
    }
}
