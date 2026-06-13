/*
package com.ichat.ichat.tasks;

import com.ichat.ichat.service.AppointmentService;
import com.ichat.ichat.service.MedicationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {
    
    private final MedicationService medicationService;
    private final AppointmentService appointmentService;
    
    public ScheduledTasks(MedicationService medicationService, AppointmentService appointmentService) {
        this.medicationService = medicationService;
        this.appointmentService = appointmentService;
    }
    
    // Run every hour to check for medication reminders
    @Scheduled(cron = "0 0 * * * ?")
    public void sendMedicationReminders() {
        medicationService.sendMedicationReminders();
    }
    
    // Run daily at 9 AM to send appointment reminders
    @Scheduled(cron = "0 0 9 * * ?")
    public void sendAppointmentReminders() {
        appointmentService.sendAppointmentReminders();
    }
}*/
