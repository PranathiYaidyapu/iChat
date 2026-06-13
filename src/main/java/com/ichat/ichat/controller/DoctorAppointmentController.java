package com.ichat.ichat.controller;

import com.ichat.ichat.model.Appointment;
import com.ichat.ichat.model.Demographics;
import com.ichat.ichat.model.Doctor;
import com.ichat.ichat.model.MedicalHistory;
import com.ichat.ichat.model.User;
import com.ichat.ichat.repository.AppointmentRepository;
import com.ichat.ichat.repository.DoctorRepository;
import com.ichat.ichat.service.DemographicsService;
import com.ichat.ichat.service.MedicalHistoryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/doctor/appointments")
public class DoctorAppointmentController {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final MedicalHistoryService medicalHistoryService;
    private final DemographicsService demographicsService;

    public DoctorAppointmentController(AppointmentRepository appointmentRepository,
                                     DoctorRepository doctorRepository,
                                     MedicalHistoryService medicalHistoryService,
                                     DemographicsService demographicsService) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.medicalHistoryService = medicalHistoryService;
        this.demographicsService = demographicsService;
    }

    @GetMapping
    public String listDoctorAppointments(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !user.getRoles().contains("ROLE_DOCTOR")) {
            return "redirect:/login";
        }

        // Get the doctor associated with the logged-in user
        Doctor doctor = doctorRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        // Get upcoming appointments (scheduled and not yet started)
        List<Appointment> upcomingAppointments = appointmentRepository
                .findByDoctorAndStartTimeAfterAndStatusOrderByStartTimeAsc(
                        doctor, now, Appointment.Status.SCHEDULED);

        // Get today's appointments
        List<Appointment> todaysAppointments = appointmentRepository
                .findByDoctorAndAppointmentDateAndStatusOrderByStartTimeAsc(
                        doctor, today, Appointment.Status.SCHEDULED);

        // Get past appointments (completed or cancelled)
        List<Appointment> pastAppointments = appointmentRepository
                .findByDoctorAndStartTimeBeforeOrderByStartTimeDesc(
                        doctor, now);

        model.addAttribute("doctor", doctor);
        model.addAttribute("upcomingAppointments", upcomingAppointments);
        model.addAttribute("todaysAppointments", todaysAppointments);
        model.addAttribute("pastAppointments", pastAppointments);
        model.addAttribute("today", today);

        return "doctor/appointments/list";
    }

    @GetMapping("/{id}")
    public String viewAppointmentDetails(@PathVariable Long id,
                                        HttpSession session,
                                        Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !user.getRoles().contains("ROLE_DOCTOR")) {
            return "redirect:/login";
        }

        Doctor doctor = doctorRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        // Verify the appointment belongs to this doctor
        if (!appointment.getDoctor().getId().equals(doctor.getId())) {
            return "redirect:/doctor/appointments";
        }

        // Get patient details
        User patient = appointment.getPatient();
        
        // Get patient's medical history
        Optional<MedicalHistory> medicalHistory = medicalHistoryService.getMedicalHistoryByUser(patient);
        
        // Get patient's demographics
        Optional<Demographics> demographics = demographicsService.getDemographicsByUser(patient);

        model.addAttribute("doctor", doctor);
        model.addAttribute("appointment", appointment);
        model.addAttribute("patient", patient);
        model.addAttribute("medicalHistory", medicalHistory.orElse(null));
        model.addAttribute("demographics", demographics.orElse(null));

        return "doctor/appointments/details";
    }

    @PostMapping("/{id}/complete")
    public String completeAppointment(@PathVariable Long id,
                                     HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !user.getRoles().contains("ROLE_DOCTOR")) {
            return "redirect:/login";
        }

        Doctor doctor = doctorRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        // Verify the appointment belongs to this doctor
        if (!appointment.getDoctor().getId().equals(doctor.getId())) {
            return "redirect:/doctor/appointments";
        }

        // Only allow completing scheduled appointments
        if (appointment.getStatus() == Appointment.Status.SCHEDULED) {
            appointment.setStatus(Appointment.Status.COMPLETED);
            appointmentRepository.save(appointment);
        }

        return "redirect:/doctor/appointments";
    }

    @GetMapping("/search")
    public String searchAppointments(@RequestParam(required = false) String patientName,
                                    @RequestParam(required = false) String date,
                                    HttpSession session,
                                    Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !user.getRoles().contains("ROLE_DOCTOR")) {
            return "redirect:/login";
        }

        Doctor doctor = doctorRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        List<Appointment> appointments = new ArrayList<>();

        if (patientName != null && !patientName.trim().isEmpty()) {

            appointments = appointmentRepository.findByDoctorAndPatientNameContainingIgnoreCase(
                    doctor, patientName.trim());
        } else if (date != null && !date.trim().isEmpty()) {

            LocalDate searchDate = LocalDate.parse(date);
            appointments = appointmentRepository.findByDoctorAndAppointmentDateAndStatusOrderByStartTimeAsc(
                    doctor, searchDate, Appointment.Status.SCHEDULED);
        } else {
            // Default: today's appointments
            appointments = appointmentRepository.findByDoctorAndAppointmentDateAndStatusOrderByStartTimeAsc(
                    doctor, LocalDate.now(), Appointment.Status.SCHEDULED);
        }

        model.addAttribute("doctor", doctor);
        model.addAttribute("appointments", appointments);
        model.addAttribute("patientName", patientName);
        model.addAttribute("date", date);

        return "doctor/appointments/search";
    }
}