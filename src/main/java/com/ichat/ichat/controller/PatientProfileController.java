package com.ichat.ichat.controller;

import com.ichat.ichat.model.Demographics;
import com.ichat.ichat.model.MedicalHistory;
import com.ichat.ichat.model.User;
import com.ichat.ichat.service.DemographicsService;
import com.ichat.ichat.service.MedicalHistoryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/patient/profile")
public class PatientProfileController {
    
    private final DemographicsService demographicsService;
    private final MedicalHistoryService medicalHistoryService;
    
    public PatientProfileController(DemographicsService demographicsService, 
                                  MedicalHistoryService medicalHistoryService) {
        this.demographicsService = demographicsService;
        this.medicalHistoryService = medicalHistoryService;
    }
    
    @GetMapping("/demographics")
    public String showDemographicsForm(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !user.getRoles().contains("ROLE_PATIENT")) {
            return "redirect:/login";
        }
        
        Demographics demographics = demographicsService.getDemographicsByUser(user)
                .orElse(new Demographics(user));
        
        model.addAttribute("user", user);
        model.addAttribute("demographics", demographics);
        
        return "patient/demographics-form";
    }
    
    @PostMapping("/demographics")
    public String saveDemographics(@ModelAttribute Demographics demographics,
                                  HttpSession session,
                                  Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !user.getRoles().contains("ROLE_PATIENT")) {
            return "redirect:/login";
        }
        
        try {
            demographicsService.createOrUpdateDemographics(user, demographics);
            model.addAttribute("success", "Demographics updated successfully!");
        } catch (Exception e) {
            model.addAttribute("error", "Error updating demographics: " + e.getMessage());
        }
        
        model.addAttribute("user", user);
        model.addAttribute("demographics", demographics);
        return "patient/demographics-form";
    }

    @GetMapping("/medical-history")
    public String showMedicalHistoryForm(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !user.getRoles().contains("ROLE_PATIENT")) {
            return "redirect:/login";
        }

        MedicalHistory medicalHistory = medicalHistoryService.getMedicalHistoryByUser(user)
                .orElse(new MedicalHistory(user));


        if (medicalHistory.getConditions() != null && !medicalHistory.getConditions().isEmpty()) {
            model.addAttribute("conditionsString", String.join(", ", medicalHistory.getConditions()));
        } else {
            model.addAttribute("conditionsString", "");
        }

        if (medicalHistory.getCurrentMedications() != null && !medicalHistory.getCurrentMedications().isEmpty()) {
            model.addAttribute("medicationsString", String.join(", ", medicalHistory.getCurrentMedications()));
        } else {
            model.addAttribute("medicationsString", "");
        }

        if (medicalHistory.getAllergies() != null && !medicalHistory.getAllergies().isEmpty()) {
            model.addAttribute("allergiesString", String.join(", ", medicalHistory.getAllergies()));
        } else {
            model.addAttribute("allergiesString", "");
        }

        // Ensure lists are never null
        if (medicalHistory.getConditions() == null) {
            medicalHistory.setConditions(new ArrayList<>());
        }
        if (medicalHistory.getCurrentMedications() == null) {
            medicalHistory.setCurrentMedications(new ArrayList<>());
        }
        if (medicalHistory.getAllergies() == null) {
            medicalHistory.setAllergies(new ArrayList<>());
        }

        model.addAttribute("user", user);
        model.addAttribute("medicalHistory", medicalHistory);

        return "patient/medical-history-form";
    }

    @PostMapping("/medical-history")
    public String saveMedicalHistory(@ModelAttribute("medicalHistory") MedicalHistory medicalHistory,
                                     @RequestParam(required = false) String conditionsString,
                                     @RequestParam(required = false) String medicationsString,
                                     @RequestParam(required = false) String allergiesString,
                                     @RequestParam(value = "lastPhysicalExam", required = false) String lastPhysicalExamStr,
                                     HttpSession session,
                                     Model model) {

        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !user.getRoles().contains("ROLE_PATIENT")) {
            return "redirect:/login";
        }

        try {

            medicalHistory.setUser(user);


            if (conditionsString != null && !conditionsString.trim().isEmpty()) {
                List<String> conditions = Arrays.stream(conditionsString.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());
                medicalHistory.setConditions(conditions);
            } else {
                medicalHistory.setConditions(new ArrayList<>());
            }

            if (medicationsString != null && !medicationsString.trim().isEmpty()) {
                List<String> medications = Arrays.stream(medicationsString.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());
                medicalHistory.setCurrentMedications(medications);
            } else {
                medicalHistory.setCurrentMedications(new ArrayList<>());
            }

            if (allergiesString != null && !allergiesString.trim().isEmpty()) {
                List<String> allergies = Arrays.stream(allergiesString.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());
                medicalHistory.setAllergies(allergies);
            } else {
                medicalHistory.setAllergies(new ArrayList<>());
            }


            if (lastPhysicalExamStr != null && !lastPhysicalExamStr.trim().isEmpty()) {
                try {
                    medicalHistory.setLastPhysicalExam(LocalDate.parse(lastPhysicalExamStr));
                } catch (Exception e) {

                    System.err.println("Error parsing date: " + e.getMessage());
                }
            }

            medicalHistoryService.createOrUpdateMedicalHistory(user, medicalHistory);
            model.addAttribute("success", "Medical history updated successfully!");


            model.addAttribute("conditionsString", conditionsString);
            model.addAttribute("medicationsString", medicationsString);
            model.addAttribute("allergiesString", allergiesString);

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error updating medical history: " + e.getMessage());
        }


        model.addAttribute("user", user);
        model.addAttribute("medicalHistory", medicalHistory);

        return "patient/medical-history-form";
    }

    @GetMapping("/view")
    public String viewProfile(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !user.getRoles().contains("ROLE_PATIENT")) {
            return "redirect:/login";
        }

        Demographics demographics = demographicsService.getDemographicsByUser(user).orElse(null);
        MedicalHistory medicalHistory = medicalHistoryService.getMedicalHistoryByUser(user).orElse(null);


        if (medicalHistory != null) {
            if (medicalHistory.getConditions() == null) {
                medicalHistory.setConditions(new ArrayList<>());
            }
            if (medicalHistory.getCurrentMedications() == null) {
                medicalHistory.setCurrentMedications(new ArrayList<>());
            }
            if (medicalHistory.getAllergies() == null) {
                medicalHistory.setAllergies(new ArrayList<>());
            }
        }

        model.addAttribute("user", user);
        model.addAttribute("demographics", demographics);
        model.addAttribute("medicalHistory", medicalHistory);

        return "patient/profile-view";
    }

    @ModelAttribute("medicalHistory")
    public MedicalHistory getMedicalHistory(@RequestParam(value = "id", required = false) Long id,
                                            HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return new MedicalHistory();
        }

        if (id != null) {

            return medicalHistoryService.findById(id).orElse(new MedicalHistory(user));
        } else {

            return medicalHistoryService.getMedicalHistoryByUser(user).orElse(new MedicalHistory(user));
        }
    }
}