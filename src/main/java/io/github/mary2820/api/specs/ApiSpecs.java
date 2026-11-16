package io.github.mary2820.api.specs;

import io.github.mary2820.config.Config;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static io.github.mary2820.constants.IConstants.OK;

public final class ApiSpecs {
    private static final String TEXT_HTML = "text/html";

    private ApiSpecs() {
    }

    // the request is logged only when the test fails, the log of a passing test is not read
    public static RequestSpecification request() {
        return new RequestSpecBuilder()
                .setBaseUri(Config.baseUrl())
                .setAccept(ContentType.JSON)
                .log(LogDetail.URI)
                .build();
    }

    // the HTTP status is 200 even for API errors, the real status is responseCode in the body,
    // so this spec checks the transport only and a test checks responseCode itself;
    // the site sends JSON as text/html, so body() reads it through jsonPath, which does not depend on the type
    public static ResponseSpecification response() {
        return new ResponseSpecBuilder()
                .expectStatusCode(OK)
                .expectContentType(TEXT_HTML)
                .build();
    }

    public static <T> T body(ValidatableResponse response, Class<T> type) {
        return response.extract().jsonPath().getObject("", type);
    }
}
