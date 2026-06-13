package com.ichat.ichat.service;

public interface EmailService {
    void sendMedicationReminder(String toEmail, String patientName, String medicationName, 
                              String dosage, String instructions);
    void sendAppointmentReminder(String toEmail, String patientName, String doctorName, 
                               String appointmentTime, String reason);
}