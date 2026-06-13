package com.ichat.ichat.controller;

import com.ichat.ichat.model.*;
import com.ichat.ichat.repository.AppointmentRepository;
import com.ichat.ichat.repository.DoctorRepository;
import com.ichat.ichat.service.AppointmentService;
import com.ichat.ichat.service.DoctorScheduleService;
import com.ichat.ichat.service.DoctorService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/appointments")
public class AppointmentController {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private DoctorScheduleService doctorScheduleService;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorService doctorService;
    @Autowired
    private AppointmentService appointmentService;


    // Show all doctors for patient to choose
    @GetMapping("/doctors")
    public String listDoctors(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !user.getRoles().contains("ROLE_PATIENT")) {
            return "redirect:/login";
        }
        List<Doctor> doctors = doctorRepository.findAll();
        model.addAttribute("doctors", doctors);
        return "appointments/doctors";
    }

    // Show available slots for selected doctor
    @GetMapping("/{doctorId}/slots")
    public String showAvailableSlots(@PathVariable Long doctorId,
                                     @RequestParam(required = false) String date,
                                     Model model, HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !user.getRoles().contains("ROLE_PATIENT")) {
            return "redirect:/login";
        }

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        LocalDate selectedDate = (date != null) ? LocalDate.parse(date) : LocalDate.now();
        List<LocalDateTime> availableSlots = doctorScheduleService.getAvailableSlots(doctor, selectedDate);

        model.addAttribute("doctor", doctor);
        model.addAttribute("selectedDate", selectedDate);
        model.addAttribute("availableSlots", availableSlots);
        return "appointments/slots";
    }

    // Book appointment
   /* @PostMapping("/book")
    public String bookAppointment(@RequestParam Long doctorId,
                                  @RequestParam String date,
                                  @RequestParam String time,
                                  @RequestParam String reason,
                                  HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !user.getRoles().contains("ROLE_PATIENT")) {
            return "redirect:/login";
        }

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        // Combine date + time into a LocalDateTime
        LocalDateTime start = LocalDateTime.parse(date + "T" + time);
        LocalDateTime end = start.plusMinutes(30);

        Appointment appointment = new Appointment();
        appointment.setDoctor(doctor);
        appointment.setPatient(user);
        appointment.setStartTime(start);
        appointment.setEndTime(end);
        appointment.setReason(reason);
        appointment.setStatus(Appointment.Status.SCHEDULED);

        appointmentRepository.save(appointment);

        return "redirect:/appointments";
    }*/
    @PostMapping("/book")
    public String bookAppointment(@RequestParam Long doctorId,
                                  @RequestParam String date,
                                  @RequestParam String time,
                                  @RequestParam String reason,
                                  HttpSession session,
                                  Model model){
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !user.getRoles().contains("ROLE_PATIENT")) {
            return "redirect:/login";
        }

        try {
            LocalDateTime start =LocalDateTime.parse(date + "T" + time);
            appointmentService.bookAppointment(user, doctorId, start, reason);
            return "redirect:/appointments";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }



    @GetMapping("/book/{doctorId}")
    public String showBookingForm(@PathVariable Long doctorId,
                                  @RequestParam(required = false) String date,
                                  Model model,
                                  HttpSession session) {

        Object loggedInUser = session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }

        Doctor doctor = doctorService.getDoctorById(doctorId)
                .orElseThrow(() -> new RuntimeException("Unable to find Doctor"));
        model.addAttribute("doctor", doctor);


        LocalDate selectedDate = (date != null) ? LocalDate.parse(date) : LocalDate.now();
        List<LocalDateTime> availableSlots = doctorScheduleService.getAvailableSlots(doctor, selectedDate);
        model.addAttribute("selectedDate", selectedDate);
        model.addAttribute("availableSlots", availableSlots);

        return "patient/book-appointment";
    }





    @GetMapping
    public String listAppointments(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !user.getRoles().contains("ROLE_PATIENT")) {
            return "redirect:/login";
        }

        LocalDate today = LocalDate.now();

        // Get upcoming appointments (scheduled and date >= today)
        List<Appointment> scheduledAppointments = appointmentRepository.findUpcomingAppointments(user, today);

        // Get cancelled appointments
        List<Appointment> cancelledAppointments = appointmentRepository.findByPatientAndStatusOrderByStartTime(
                user, Appointment.Status.CANCELLED);

        // Get past appointments (for info only)
        List<Appointment> pastAppointments = appointmentRepository.findPastAppointments(user, today);

        model.addAttribute("today", today);
        model.addAttribute("scheduledAppointments", scheduledAppointments);
        model.addAttribute("cancelledAppointments", cancelledAppointments);
        model.addAttribute("pastAppointments", pastAppointments);

        // For backward compatibility
        model.addAttribute("appointments", scheduledAppointments);

        return "appointments/list";
    }
    @PostMapping("/{id}/cancel")
    public String cancelAppointment(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !user.getRoles().contains("ROLE_PATIENT")) {
            return "redirect:/login";
        }

        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (!appointment.getPatient().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized cancellation attempt");
        }


        if (appointment.getStatus() == Appointment.Status.SCHEDULED) {
            appointment.setStatus(Appointment.Status.CANCELLED);
            appointmentRepository.save(appointment);
        }

        return "redirect:/appointments";
    }
    @GetMapping("/sendAppointmentReminders")
    @ResponseBody
    public void sendAppointmentReminders() {
        appointmentService.sendAppointmentReminders();
    }

}
