package io.github.mary2820.tests.api;

import io.github.mary2820.api.specs.ApiSpecs;
import io.github.mary2820.base.BaseApiTest;
import io.github.mary2820.models.Brand;
import io.github.mary2820.models.BrandsResponse;
import io.github.mary2820.models.Product;
import io.github.mary2820.models.ProductsResponse;
import io.qase.commons.annotation.QaseId;
import org.testng.annotations.Test;

import static io.github.mary2820.assertions.CustomAssert.assertEquals;
import static io.github.mary2820.assertions.CustomAssert.assertFalse;
import static io.github.mary2820.constants.IConstants.*;
import static io.restassured.RestAssured.given;

public class CatalogApiTest extends BaseApiTest {

    @Test(description = "GET /productsList returns the product list", groups = PRIORITY_HIGH)
    @QaseId(37)
    public void productsListReturnsProductsTest() {
        ProductsResponse body = ApiSpecs.body(given().spec(ApiSpecs.request())
                .get(PRODUCTS_LIST_ENDPOINT)
                .then().spec(ApiSpecs.response()), ProductsResponse.class);

        assertEquals(body.responseCode(), OK, "%sresponseCode should be %s", PAGE_NAME_PRODUCT_API, OK);
        assertFalse(body.products().isEmpty(), "%sproducts should not be empty", PAGE_NAME_PRODUCT_API);
        for (Product product : body.products()) {
            assertFalse(product.name() == null || product.name().isBlank(),
                    "%sProduct %s should have a name", PAGE_NAME_PRODUCT_API, product.id());
            assertFalse(product.price() == null || product.price().isBlank(),
                    "%sProduct %s should have a price", PAGE_NAME_PRODUCT_API, product.id());
            assertFalse(product.brand() == null || product.brand().isBlank(),
                    "%sProduct %s should have a brand", PAGE_NAME_PRODUCT_API, product.id());
            assertFalse(product.category() == null, "%sProduct %s should have a category",
                    PAGE_NAME_PRODUCT_API, product.id());
        }
    }

    @Test(description = "GET /brandsList returns the brand list", groups = PRIORITY_HIGH)
    @QaseId(39)
    public void brandsListReturnsBrandsTest() {
        BrandsResponse body = ApiSpecs.body(given().spec(ApiSpecs.request())
                .get(BRANDS_LIST_ENDPOINT)
                .then().spec(ApiSpecs.response()), BrandsResponse.class);

        assertEquals(body.responseCode(), OK, "%sresponseCode should be %s", PAGE_NAME_BRAND_API, OK);
        assertFalse(body.brands().isEmpty(), "%sbrands should not be empty", PAGE_NAME_BRAND_API);
        for (Brand brand : body.brands()) {
            assertFalse(brand.brand() == null || brand.brand().isBlank(),
                    "%sBrand %s should have a name", PAGE_NAME_BRAND_API, brand.id());
        }
    }
}
