package com.ichat.ichat.controller;

import com.ichat.ichat.model.Doctor;
import com.ichat.ichat.model.User;
import com.ichat.ichat.service.DoctorService;
import com.ichat.ichat.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/doctor")
public class DoctorController {
    private DoctorService doctorService;
    private UserService userService;

    DoctorController(UserService userService, DoctorService doctorService) {
        this.userService = userService;
        this.doctorService = doctorService;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !user.getRoles().contains("ROLE_DOCTOR")) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        return "doctor/dashboard";
    }
    @GetMapping("/profile")
    public String updateProfile(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !user.getRoles().contains("ROLE_DOCTOR")) {
            return "redirect:/login";
        }
        Doctor doctor = doctorService.getDoctorById(user.getDoctor().getId()).orElseThrow(()->new RuntimeException("Unable to Find Doctor"));
        model.addAttribute("user", user);
        model.addAttribute("doctor", doctor);
        return "doctor/update_profile";
    }
    @PostMapping("/update_profile")
    public String updateProfile(HttpSession session,
                                @RequestParam Long id,
                                @RequestParam String firstname,
                                @RequestParam String lastName,
                                @RequestParam String designation,
                                @RequestParam String specialization ,
                                Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !user.getRoles().contains("ROLE_DOCTOR")) {
            return "redirect:/login";
        }

        user.setFirstname(firstname);
        user.setLastName(lastName);
        userService.updateUser(user);
        Doctor doctor = doctorService.getDoctorById(user.getDoctor().getId()).orElseThrow(()->new RuntimeException("Unable to find Doctor with userId "+id));
        doctor.setDesignation(designation);
        doctor.setSpecialization(specialization);
        Doctor updatedDoctor = doctorService.saveDoctor(doctor);

        return "redirect:/doctor/dashboard";
    }

}
