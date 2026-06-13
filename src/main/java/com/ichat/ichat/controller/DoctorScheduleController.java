package com.ichat.ichat.controller;

import com.ichat.ichat.model.Doctor;
import com.ichat.ichat.model.DoctorSchedule;
import com.ichat.ichat.model.User;
import com.ichat.ichat.repository.DoctorRepository;
import com.ichat.ichat.service.DoctorScheduleService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/doctor/schedule")
public class DoctorScheduleController {

    private final DoctorScheduleService scheduleService;

    @Autowired
    private DoctorRepository doctorRepository;

    public DoctorScheduleController(DoctorScheduleService scheduleService) {
        this.scheduleService = scheduleService;


    }

    @GetMapping
    public String listSchedules(HttpSession session, Model model) {

        if(!doctorLoggedIn(session))
            return "redirect:/login";

        Optional<Doctor> doctor = doctorRepository.findByUser(getLoggedInUser(session));

        List<DoctorSchedule> schedules = scheduleService.getSchedulesForDoctor(doctor);
        model.addAttribute("schedules", schedules);
        model.addAttribute("days", Arrays.asList(java.time.DayOfWeek.values()));
        return "doctor/schedule/list";
    }

    @GetMapping("/new")
    public String newSchedule(HttpSession session,Model model) {
        if(!doctorLoggedIn(session))
            return "redirect:/login";
        model.addAttribute("schedule", new DoctorSchedule());
        model.addAttribute("days", Arrays.asList(java.time.DayOfWeek.values()));
        return "doctor/schedule/form";
    }

    @PostMapping
    public String saveSchedule(@ModelAttribute DoctorSchedule schedule, HttpSession session) {
        Doctor doctor = doctorRepository.findByUser(getLoggedInUser(session)).orElseThrow(()->new RuntimeException("Unable to FInd Doctor"));
        if (doctor == null) return "redirect:/login";

        schedule.setDoctor(doctor);
        scheduleService.saveSchedule(schedule);
        return "redirect:/doctor/schedule";
    }

    @PostMapping("/{id}/delete")
    public String deleteSchedule(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return "redirect:/doctor/schedule";
    }
    private boolean doctorLoggedIn(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        return !(user == null || !user.getRoles().contains("ROLE_DOCTOR"));

    }
    private User getLoggedInUser(HttpSession session) {
        return (User) session.getAttribute("loggedInUser");
    }
}
