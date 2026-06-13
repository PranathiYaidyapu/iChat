package com.ichat.ichat.controller;

import com.ichat.ichat.model.User;
import com.ichat.ichat.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class LoginController {

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        @RequestParam String role,
                        HttpSession session,
                        Model model) {

        User user = userService.findByUsername(username);
        if (user == null) {
            model.addAttribute("error", "Invalid username/password");
            return "login";
        }

        if (!user.getPassword().equals(password)) { // use password encoder in real apps!
            model.addAttribute("error", "Invalid username/password");
            return "login";
        }


        boolean roleMatch = user.getRoles().stream()
                .anyMatch(r -> r.equalsIgnoreCase(role));
        if (!roleMatch) {
            model.addAttribute("error", "Invalid username/password  ");
            return "login";
        }

        // Save user in session
        session.setAttribute("loggedInUser", user);

        // Redirect based on role
        return switch (role.toUpperCase()) {
            case "ROLE_ADMIN" -> "redirect:/admin/dashboard";
            case "ROLE_DOCTOR" -> "redirect:/doctor/dashboard";
            case "ROLE_PATIENT" -> "redirect:/home";
            default -> "login";
        };
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
