package com.ichat.ichat.controller;

import com.ichat.ichat.model.User;
import com.ichat.ichat.repository.AppointmentRepository;
import com.ichat.ichat.repository.DoctorRepository;
import com.ichat.ichat.service.AppointmentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/patient")
public class PatientController {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private AppointmentService appointmentService;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !user.getRoles().contains("ROLE_PATIENT")) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        model.addAttribute("upcomingAppointments", appointmentService.getUpcomingAppointments(user));
        model.addAttribute("pastAppointments", appointmentService.getPastAppointments(user));

        return "patient/dashboard";
    }

    @GetMapping("/appointment/new")
    public String newAppointment() {
        return "redirect:/patient/doctors"; // this will show available doctors
    }
}
