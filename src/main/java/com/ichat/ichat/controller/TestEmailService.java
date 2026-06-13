package com.ichat.ichat.controller;

import com.ichat.ichat.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestEmailService {
    @Autowired
    EmailService emailService;
    @GetMapping("/testemail")
    public void sendEmail(){
        emailService.sendAppointmentReminder("jpgpgma666@gmal.com","vijay","DR.Prasad","10:29","please take");
        emailService.sendMedicationReminder("malireddy.666@gmail.com","vijay","DR.Prasad","10:29","please take");

    }
}
