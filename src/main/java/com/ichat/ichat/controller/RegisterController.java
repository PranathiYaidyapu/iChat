package com.ichat.ichat.controller;

import com.ichat.ichat.model.User;
import com.ichat.ichat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class RegisterController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String showRegistrationForm() {
        return "register"; // Thymeleaf template name
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String firstName,
                               @RequestParam String lastName,
                               @RequestParam String username,
                               @RequestParam String password,
                               Model model) {
        try {
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setFirstName(firstName);
            user.setLastName(lastName);

            userService.registerUser(user);

            model.addAttribute("success", true);
            return "register";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Email already exists or some error occurred");
            return "register";
        }
    }
}
