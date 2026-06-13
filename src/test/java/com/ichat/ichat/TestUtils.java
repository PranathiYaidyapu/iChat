package com.ichat.ichat;

import com.ichat.ichat.model.User;

public class TestUtils {

    public static User createValidUser() {
        User user = new User();
        user.setUsername("test@example.com");
        user.setPassword("password123");
        user.setFirstname("Test");
        user.setLastName("User");
        return user;
    }

    public static User createValidUser(String email, String firstName, String lastName) {
        User user = new User();
        user.setUsername(email);
        user.setPassword("password123");
        user.setFirstname(firstName);
        user.setLastName(lastName);
        return user;
    }
}