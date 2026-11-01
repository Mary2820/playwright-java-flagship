package io.github.mary2820.models;

// only the fields createAccount requires, title, birth date, company and address2 are optional
public record TestUser(String name, String email, String password,
                       String firstName, String lastName,
                       String address1, String country, String zipcode,
                       String state, String city, String mobileNumber) {
}
