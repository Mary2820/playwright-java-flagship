package io.github.mary2820.tests.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.function.Consumer;

import static io.github.mary2820.assertions.CustomAssert.assertFalse;
import static io.github.mary2820.assertions.CustomAssert.assertTrue;
import static io.github.mary2820.constants.IConstants.*;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

// a plain test of the schemas themselves: no network and no browser, so it does not extend BaseApiTest;
// a schema that accepts a broken response would make the API tests that use it pass for nothing
public class JsonSchemaTest {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test(description = "JSON Schemas accept a valid response", groups = PRIORITY_MEDIUM,
            dataProvider = "schemas")
    public void validResponseIsAcceptedTest(String schema, String json) {
        assertTrue(matchesJsonSchemaInClasspath(schema).matches(json),
                "%sSchema %s should accept a valid response", PAGE_NAME_SCHEMA, schema);
    }

    @Test(description = "JSON Schemas reject a broken response", groups = PRIORITY_MEDIUM,
            dataProvider = "brokenResponses")
    public void brokenResponseIsRejectedTest(String name, String schema, String json) {
        assertFalse(matchesJsonSchemaInClasspath(schema).matches(json),
                "%sSchema %s should reject a response with %s", PAGE_NAME_SCHEMA, schema, name);
    }

    @DataProvider
    public Object[][] schemas() {
        return new Object[][]{
                {SEARCH_SCHEMA, SEARCH_RESPONSE},
                {LOGIN_SCHEMA, LOGIN_RESPONSE}
        };
    }

    @DataProvider
    public Object[][] brokenResponses() {
        return new Object[][]{
                broken("no responseCode", SEARCH_SCHEMA, SEARCH_RESPONSE, n -> n.remove("responseCode")),
                broken("responseCode as a string", SEARCH_SCHEMA, SEARCH_RESPONSE, n -> n.put("responseCode", "200")),
                broken("no products", SEARCH_SCHEMA, SEARCH_RESPONSE, n -> n.remove("products")),
                broken("products as an object", SEARCH_SCHEMA, SEARCH_RESPONSE, n -> n.putObject("products")),
                broken("products as null", SEARCH_SCHEMA, SEARCH_RESPONSE, n -> n.putNull("products")),
                broken("a product without name", SEARCH_SCHEMA, SEARCH_RESPONSE, n -> product(n).remove("name")),
                broken("a product id as a string", SEARCH_SCHEMA, SEARCH_RESPONSE, n -> product(n).put("id", "1")),
                broken("a product price as a number", SEARCH_SCHEMA, SEARCH_RESPONSE, n -> product(n).put("price", 500)),
                broken("a product without category", SEARCH_SCHEMA, SEARCH_RESPONSE, n -> product(n).remove("category")),
                broken("a category without usertype", SEARCH_SCHEMA, SEARCH_RESPONSE,
                        n -> product(n).with("category").remove("usertype")),
                broken("a product that is not an object", SEARCH_SCHEMA, SEARCH_RESPONSE,
                        n -> n.withArray("products").add("text")),
                broken("no accessToken", LOGIN_SCHEMA, LOGIN_RESPONSE, n -> n.remove("accessToken")),
                broken("an empty accessToken", LOGIN_SCHEMA, LOGIN_RESPONSE, n -> n.put("accessToken", "")),
                broken("accessToken as a number", LOGIN_SCHEMA, LOGIN_RESPONSE, n -> n.put("accessToken", 1)),
                broken("no refreshToken", LOGIN_SCHEMA, LOGIN_RESPONSE, n -> n.remove("refreshToken")),
                broken("id as a string", LOGIN_SCHEMA, LOGIN_RESPONSE, n -> n.put("id", "1")),
                broken("id as a fraction", LOGIN_SCHEMA, LOGIN_RESPONSE, n -> n.put("id", 1.5)),
                broken("no username", LOGIN_SCHEMA, LOGIN_RESPONSE, n -> n.remove("username")),
                broken("username as null", LOGIN_SCHEMA, LOGIN_RESPONSE, n -> n.putNull("username")),
                broken("no email", LOGIN_SCHEMA, LOGIN_RESPONSE, n -> n.remove("email"))
        };
    }

    private static Object[] broken(String name, String schema, String valid, Consumer<ObjectNode> breakIt) {
        try {
            ObjectNode node = (ObjectNode) MAPPER.readTree(valid);
            breakIt.accept(node);
            return new Object[]{name, schema, MAPPER.writeValueAsString(node)};
        } catch (Exception e) {
            throw new IllegalStateException("Cannot build the broken response: " + name, e);
        }
    }

    private static ObjectNode product(ObjectNode response) {
        JsonNode first = response.get("products").get(0);
        return (ObjectNode) first;
    }
}
