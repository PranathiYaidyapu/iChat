package com.ichat.ichat.controller;

import com.ichat.ichat.model.Medication;
import com.ichat.ichat.model.User;
import com.ichat.ichat.service.MedicationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/patient/medications")
public class MedicationController {
    
    private final MedicationService medicationService;
    
    public MedicationController(MedicationService medicationService) {
        this.medicationService = medicationService;
    }
    
    @GetMapping
    public String listMedications(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !user.getRoles().contains("ROLE_PATIENT")) {
            return "redirect:/login";
        }
        
        List<Medication> medications = medicationService.getActiveMedicationsByPatient(user);
        model.addAttribute("medications", medications);
        model.addAttribute("user", user);
        
        return "patient/medications/list";
    }
    

    @GetMapping("/new")
    public String showMedicationForm(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !user.getRoles().contains("ROLE_PATIENT")) {
            return "redirect:/login";
        }
        
        model.addAttribute("medication", new Medication());
        model.addAttribute("user", user);
        
        return "patient/medications/form";
    }
    
    @PostMapping
    public String saveMedication(@ModelAttribute Medication medication, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !user.getRoles().contains("ROLE_PATIENT")) {
            return "redirect:/login";
        }
        
        medication.setPatient(user);
        medicationService.saveMedication(medication);
        
        return "redirect:/patient/medications";
    }
    
    @GetMapping("/{id}/edit")
    public String editMedication(@PathVariable Long id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !user.getRoles().contains("ROLE_PATIENT")) {
            return "redirect:/login";
        }
        
        Medication medication = medicationService.getMedicationById(id);
        if (medication == null || !medication.getPatient().getId().equals(user.getId())) {
            return "redirect:/patient/medications";
        }
        
        model.addAttribute("medication", medication);
        model.addAttribute("user", user);
        
        return "patient/medications/form";
    }
    
    @PostMapping("/{id}")
    public String updateMedication(@PathVariable Long id, @ModelAttribute Medication medication, 
                                  HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !user.getRoles().contains("ROLE_PATIENT")) {
            return "redirect:/login";
        }
        
        medicationService.updateMedication(id, medication);
        return "redirect:/patient/medications";
    }
    
    @PostMapping("/{id}/delete")
    public String deleteMedication(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !user.getRoles().contains("ROLE_PATIENT")) {
            return "redirect:/login";
        }
        
        medicationService.deleteMedication(id);
        return "redirect:/patient/medications";
    }
    
    @PostMapping("/{id}/toggle")
    public String toggleMedicationStatus(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !user.getRoles().contains("ROLE_PATIENT")) {
            return "redirect:/login";
        }
        
        Medication medication = medicationService.getMedicationById(id);
        if (medication != null && medication.getPatient().getId().equals(user.getId())) {
            medication.setActive(!medication.isActive());
            medicationService.saveMedication(medication);
        }
        
        return "redirect:/patient/medications";
    }

    @GetMapping("/all")
    public String listAllMedications(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !user.getRoles().contains("ROLE_PATIENT")) {
            return "redirect:/login";
        }

        List<Medication> medications = medicationService.getMedicationsByPatient(user);
        model.addAttribute("medications", medications);
        model.addAttribute("user", user);

        return "patient/medications/all";
    }
    @GetMapping("/sendMedicationAlerts")
    @ResponseBody
        public void sendMedicationAlerts(){
            medicationService.sendMedicationReminders();
        }

}