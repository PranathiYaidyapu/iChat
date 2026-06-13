package com.ichat.ichat.controller;

import com.ichat.ichat.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String home(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null ) {
            return "redirect:/login";
        }
        String role = user.getRoles().iterator().next();
        model.addAttribute("user", user);
        return switch (role.toUpperCase()) {
            case "ROLE_ADMIN" -> "redirect:/admin/dashboard";
            case "ROLE_DOCTOR" -> "redirect:/doctor/dashboard";
            case "ROLE_PATIENT" -> "redirect:/patient/dashboard";
            default -> "login";
        };

    }
    @GetMapping("/")
    public String home() {

            return "redirect:/home";
        }


}
