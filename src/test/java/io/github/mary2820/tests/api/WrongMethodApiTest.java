package io.github.mary2820.tests.api;

import io.github.mary2820.api.specs.ApiSpecs;
import io.github.mary2820.base.BaseApiTest;
import io.qase.commons.annotation.QaseId;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static io.github.mary2820.assertions.CustomAssert.assertEquals;
import static io.github.mary2820.assertions.CustomAssert.assertTrue;
import static io.github.mary2820.constants.IConstants.*;
import static io.restassured.RestAssured.given;

public class WrongMethodApiTest extends BaseApiTest {

    @DataProvider(name = "wrongMethods")
    public Object[][] wrongMethods() {
        return new Object[][]{
                {Method.PUT, CREATE_ACCOUNT_ENDPOINT},
                {Method.DELETE, CREATE_ACCOUNT_ENDPOINT},
                {Method.POST, UPDATE_ACCOUNT_ENDPOINT},
                {Method.POST, DELETE_ACCOUNT_ENDPOINT}};
    }

    // these endpoints answer a real HTTP 405 with {"detail": ...} and no responseCode, unlike verifyLogin
    @Test(description = "Wrong HTTP method on account endpoints returns a real HTTP 405", groups = PRIORITY_MEDIUM,
            dataProvider = "wrongMethods")
    @QaseId(281)
    public void wrongMethodReturnsRealHttp405Test(Method method, String endpoint) {
        JsonPath body = given().spec(ApiSpecs.request())
                .request(method, endpoint)
                .then().spec(ApiSpecs.jsonResponse(METHOD_NOT_ALLOWED))
                .extract().jsonPath();
        String expected = METHOD_NOT_ALLOWED_DETAIL_TEMPLATE.formatted(method.name());

        assertEquals(body.getString("detail"), expected, "%s%s %s detail should be %s", PAGE_NAME_USER_API,
                method, endpoint, expected);
        assertTrue(body.get("responseCode") == null, "%s%s %s should have no responseCode", PAGE_NAME_USER_API,
                method, endpoint);
    }
}
