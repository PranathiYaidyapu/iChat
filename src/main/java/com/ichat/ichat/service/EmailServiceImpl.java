package com.ichat.ichat.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
    
    private final JavaMailSender mailSender;
    private final String fromEmail;
    
    public EmailServiceImpl(JavaMailSender mailSender, @Value("${spring.mail.username}") String fromEmail) {
        this.mailSender = mailSender;
        this.fromEmail=fromEmail;
    }
    
    @Override
    public void sendMedicationReminder(String toEmail, String patientName, String medicationName, 
                                     String dosage, String instructions) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Medication Reminder - " + medicationName);
        message.setText(
            "Hello " + patientName + ",\n\n" +
            "This is a reminder to take your medication:\n\n" +
            "Medication: " + medicationName + "\n" +
            "Dosage: " + dosage + "\n" +
            "Instructions: " + (instructions != null ? instructions : "As prescribed") + "\n\n" +
            "Stay healthy!\n" +
            "iChat Mental Health Team"
        );
        mailSender.send(message);
    }
    
    @Override
    public void sendAppointmentReminder(String toEmail, String patientName, String doctorName, 
                                      String appointmentTime, String reason) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Appointment Reminder with Dr. " + doctorName);
        message.setText(
            "Hello " + patientName + ",\n\n" +
            "This is a reminder for your upcoming appointment:\n\n" +
            "Doctor: Dr. " + doctorName + "\n" +
            "Time: " + appointmentTime + "\n" +
            "Reason: " + (reason != null ? reason : "General consultation") + "\n\n" +
            "Please join on time.\n\n" +
            "Best regards,\n" +
            "iChat Mental Health Team"
        );
        mailSender.send(message);
    }
}