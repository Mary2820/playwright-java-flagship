package io.github.mary2820.tests.api;

import io.github.mary2820.api.UserForm;
import io.github.mary2820.base.BaseApiTest;
import io.github.mary2820.factories.TestUserFactory;
import io.github.mary2820.models.ApiResponse;
import io.github.mary2820.models.TestUser;
import io.github.mary2820.models.User;
import io.github.mary2820.models.UserResponse;
import io.qase.commons.annotation.QaseId;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Map;

import static io.github.mary2820.api.AccountRequests.send;
import static io.github.mary2820.api.AccountRequests.userDetail;
import static io.github.mary2820.assertions.CustomAssert.assertEquals;
import static io.github.mary2820.constants.IConstants.*;
import static io.restassured.http.Method.DELETE;
import static io.restassured.http.Method.POST;
import static io.restassured.http.Method.PUT;

public class UserCrudApiTest extends BaseApiTest {
    private static final String CHANGED_ZIPCODE = "54321";
    private static final String CHANGED_CITY = "Changed City";
    private static final String CHANGED_LAST_NAME = "Updated";

    @DataProvider(name = "requiredFields")
    public Object[][] requiredFields() {
        return UserForm.REQUIRED_FIELDS.stream().map(field -> new Object[]{field}).toArray(Object[][]::new);
    }

    @Test(description = "POST /createAccount without a required parameter is rejected", groups = PRIORITY_MEDIUM,
            dataProvider = "requiredFields")
    @QaseId(54)
    public void createAccountWithoutRequiredParameterIsRejectedTest(String field) {
        TestUser user = TestUserFactory.random();
        // tracked, so the cleanup removes the user if the site creates it despite the missing parameter
        userService.track(user);
        Map<String, String> form = UserForm.fields(user);
        form.remove(field);
        ApiResponse rejected = new ApiResponse(BAD_REQUEST, PARAM_MISSING_TEMPLATE.formatted(field, POST.name()));

        assertEquals(send(POST, CREATE_ACCOUNT_ENDPOINT, form), rejected,
                "%sCreate account response without %s should be %s", PAGE_NAME_USER_API, field, rejected);
        // without the email there is nothing to look the user up by
        if (!field.equals(UserForm.EMAIL))
            assertEquals(userDetail(user.email()).responseCode(), NOT_FOUND,
                    "%sNo account should be created without %s", PAGE_NAME_USER_API, field);
    }

    @Test(description = "PUT /updateAccount updates a user", groups = PRIORITY_HIGH)
    @QaseId(61)
    public void updateAccountChangesUserDataTest() {
        TestUser user = userService.create();
        TestUser changed = changedCopy(user);
        ApiResponse updated = new ApiResponse(OK, ACCOUNT_UPDATED);

        assertEquals(send(PUT, UPDATE_ACCOUNT_ENDPOINT, UserForm.fields(changed)), updated,
                "%sUpdate account response should be %s", PAGE_NAME_USER_API, updated);

        // the mobile number is not part of getUserDetailByEmail, so only the returned fields are compared
        UserResponse detail = userDetail(user.email());
        User actual = detail.user();
        assertEquals(detail.responseCode(), OK, "%sresponseCode should be %s", PAGE_NAME_USER_API, OK);
        assertEquals(actual.lastName(), changed.lastName(), "%sLast name should be %s", PAGE_NAME_USER_API,
                changed.lastName());
        assertEquals(actual.address1(), changed.address1(), "%sAddress should be %s", PAGE_NAME_USER_API,
                changed.address1());
        assertEquals(actual.zipcode(), changed.zipcode(), "%sZipcode should be %s", PAGE_NAME_USER_API,
                changed.zipcode());
        assertEquals(actual.city(), changed.city(), "%sCity should be %s", PAGE_NAME_USER_API, changed.city());
    }

    @Test(description = "PUT /updateAccount for an unknown account", groups = PRIORITY_MEDIUM)
    @QaseId(62)
    public void updateAccountForUnknownEmailIsNotFoundTest() {
        ApiResponse notFound = new ApiResponse(NOT_FOUND, ACCOUNT_NOT_FOUND);

        assertEquals(send(PUT, UPDATE_ACCOUNT_ENDPOINT, UserForm.fields(TestUserFactory.random())), notFound,
                "%sUpdate response for an unregistered email should be %s", PAGE_NAME_USER_API, notFound);
    }

    @Test(description = "PUT /updateAccount with a wrong password does not change the user", groups = PRIORITY_HIGH)
    @QaseId(63)
    public void updateAccountWithWrongPasswordDoesNotChangeUserTest() {
        TestUser user = userService.create();
        Map<String, String> form = UserForm.fields(changedCopy(user));
        form.put(UserForm.PASSWORD, user.password() + "x");
        ApiResponse notFound = new ApiResponse(NOT_FOUND, ACCOUNT_NOT_FOUND);

        assertEquals(send(PUT, UPDATE_ACCOUNT_ENDPOINT, form), notFound,
                "%sUpdate response for a wrong password should be %s", PAGE_NAME_USER_API, notFound);

        User actual = userDetail(user.email()).user();
        assertEquals(actual.lastName(), user.lastName(), "%sLast name should stay %s", PAGE_NAME_USER_API,
                user.lastName());
        assertEquals(actual.address1(), user.address1(), "%sAddress should stay %s", PAGE_NAME_USER_API,
                user.address1());
        assertEquals(actual.zipcode(), user.zipcode(), "%sZipcode should stay %s", PAGE_NAME_USER_API,
                user.zipcode());
        assertEquals(actual.city(), user.city(), "%sCity should stay %s", PAGE_NAME_USER_API, user.city());
    }

    @Test(description = "DELETE /deleteAccount for an unknown account", groups = PRIORITY_MEDIUM)
    @QaseId(58)
    public void deleteAccountForUnknownAccountIsNotFoundTest() {
        TestUser unknown = TestUserFactory.random();
        ApiResponse notFound = new ApiResponse(NOT_FOUND, ACCOUNT_NOT_FOUND);

        assertEquals(send(DELETE, DELETE_ACCOUNT_ENDPOINT, UserForm.credentials(unknown.email(), unknown.password())),
                notFound, "%sDelete response for an unregistered email should be %s", PAGE_NAME_USER_API, notFound);
    }

    @Test(description = "DELETE /deleteAccount with a wrong password does not delete the account",
            groups = PRIORITY_HIGH)
    @QaseId(59)
    public void deleteAccountWithWrongPasswordDoesNotDeleteAccountTest() {
        TestUser user = userService.create();
        ApiResponse notFound = new ApiResponse(NOT_FOUND, ACCOUNT_NOT_FOUND);
        ApiResponse loggedIn = new ApiResponse(OK, USER_EXISTS);

        assertEquals(send(DELETE, DELETE_ACCOUNT_ENDPOINT, UserForm.credentials(user.email(), user.password() + "x")),
                notFound, "%sDelete response for a wrong password should be %s", PAGE_NAME_USER_API, notFound);
        assertEquals(send(POST, VERIFY_LOGIN_ENDPOINT, UserForm.credentials(user.email(), user.password())), loggedIn,
                "%sLogin after the rejected delete should be %s", PAGE_NAME_USER_API, loggedIn);
    }

    // the same user with changed fields; the email and password stay, they identify the account
    private TestUser changedCopy(TestUser user) {
        return new TestUser(user.name(), user.email(), user.password(), user.firstName(), CHANGED_LAST_NAME,
                user.address1() + " 2", user.country(), CHANGED_ZIPCODE, user.state(), CHANGED_CITY,
                user.mobileNumber());
    }
}
