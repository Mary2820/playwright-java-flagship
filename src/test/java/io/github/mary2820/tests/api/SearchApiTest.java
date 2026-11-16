package io.github.mary2820.tests.api;

import io.github.mary2820.api.specs.ApiSpecs;
import io.github.mary2820.base.BaseApiTest;
import io.github.mary2820.models.ApiResponse;
import io.github.mary2820.models.Product;
import io.github.mary2820.models.ProductsResponse;
import io.qase.commons.annotation.QaseId;
import io.restassured.response.ValidatableResponse;
import org.testng.annotations.Test;

import static io.github.mary2820.assertions.CustomAssert.assertEmpty;
import static io.github.mary2820.assertions.CustomAssert.assertEquals;
import static io.github.mary2820.assertions.CustomAssert.assertFalse;
import static io.github.mary2820.assertions.CustomAssert.assertTrue;
import static io.github.mary2820.constants.IConstants.*;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class SearchApiTest extends BaseApiTest {

    @Test(description = "POST /searchProduct returns matching products", groups = PRIORITY_HIGH)
    @QaseId(42)
    public void searchReturnsMatchingProductsTest() {
        ValidatableResponse response = search(SEARCH_TERM)
                .body(matchesJsonSchemaInClasspath("schemas/search-products-response.json"));
        ProductsResponse body = ApiSpecs.body(response, ProductsResponse.class);

        assertEquals(body.responseCode(), OK, "%sresponseCode should be %s", PAGE_NAME_SEARCH_API, OK);
        assertFalse(body.products().isEmpty(), "%sproducts for '%s' should not be empty", PAGE_NAME_SEARCH_API,
                SEARCH_TERM);
        // the site also matches by category: "Little Girls Mr. Panda Shirt" is found through "Tops & Shirts"
        for (Product product : body.products()) {
            assertTrue(matches(product, SEARCH_TERM), "%sProduct '%s' (%s) should match '%s' by name or category",
                    PAGE_NAME_SEARCH_API, product.name(), product.category().category(), SEARCH_TERM);
        }
    }

    @Test(description = "POST /searchProduct with no matches returns an empty list", groups = PRIORITY_MEDIUM)
    @QaseId(43)
    public void searchWithNoMatchesReturnsEmptyListTest() {
        ProductsResponse body = ApiSpecs.body(search(SEARCH_NO_MATCH_TERM), ProductsResponse.class);

        assertEquals(body.responseCode(), OK, "%sresponseCode should be %s", PAGE_NAME_SEARCH_API, OK);
        assertEmpty(body.products(), "%sproducts for '%s' should be empty", PAGE_NAME_SEARCH_API,
                SEARCH_NO_MATCH_TERM);
    }

    @Test(description = "POST /searchProduct without search_product is rejected", groups = PRIORITY_HIGH)
    @QaseId(44)
    public void searchWithoutParameterIsRejectedTest() {
        ApiResponse body = ApiSpecs.body(given().spec(ApiSpecs.request())
                .post(SEARCH_PRODUCT_ENDPOINT)
                .then().spec(ApiSpecs.response()), ApiResponse.class);

        assertEquals(body.responseCode(), BAD_REQUEST, "%sresponseCode should be %s", PAGE_NAME_SEARCH_API,
                BAD_REQUEST);
        assertEquals(body.message(), SEARCH_PARAM_MISSING, "%sMessage should be %s", PAGE_NAME_SEARCH_API,
                SEARCH_PARAM_MISSING);
    }

    private ValidatableResponse search(String term) {
        return given().spec(ApiSpecs.request())
                .formParam(SEARCH_PARAM, term)
                .post(SEARCH_PRODUCT_ENDPOINT)
                .then().spec(ApiSpecs.response());
    }

    private boolean matches(Product product, String term) {
        String lower = term.toLowerCase();
        return product.name().toLowerCase().contains(lower)
                || product.category().category().toLowerCase().contains(lower);
    }
}
