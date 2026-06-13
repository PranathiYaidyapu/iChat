package com.ichat.ichat.controller;

import com.ichat.ichat.model.Doctor;
import com.ichat.ichat.service.DoctorService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@Controller
public class PatientDoctorController {

    private final DoctorService doctorService;

    public PatientDoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping("/patient/doctors")
    public String showAvailableDoctors(HttpSession session, Model model) {

        Object loggedInUser = session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }

        List<Doctor> doctors = doctorService.getAllDoctors();
        model.addAttribute("doctors", doctors);

        return "patient/doctors";
    }
}
