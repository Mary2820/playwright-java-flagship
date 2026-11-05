package io.github.mary2820.factories;

import io.github.mary2820.models.TestUser;
import net.datafaker.Faker;

import java.util.UUID;

public final class TestUserFactory {
    private static final Faker FAKER = new Faker();
    // the values offered by the signup form
    private static final String[] COUNTRIES = {
            "India", "United States", "Canada", "Australia", "Israel", "New Zealand", "Singapore"};

    private TestUserFactory() {
    }

    public static TestUser random() {
        // the email must be unique per test, faker data alone can repeat
        String id = UUID.randomUUID().toString().substring(0, 8);
        String firstName = FAKER.name().firstName();
        String lastName = FAKER.name().lastName();
        return new TestUser(
                firstName + " " + lastName,
                "qa_" + id + "@example.test",
                FAKER.internet().password(8, 16, true),
                firstName,
                lastName,
                FAKER.address().streetAddress(),
                FAKER.options().option(COUNTRIES),
                FAKER.address().zipCode(),
                FAKER.address().state(),
                FAKER.address().city(),
                FAKER.phoneNumber().cellPhone());
    }
}
