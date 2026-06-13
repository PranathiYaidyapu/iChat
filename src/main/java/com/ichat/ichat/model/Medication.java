package com.ichat.ichat.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "patient_medications")
public class Medication {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;
    
    @Column(nullable = false)
    private String medicationName;
    
    private String dosage;
    
    private String frequency;
    
    @Column(name = "start_date")
    private LocalDate startDate;
    
    @Column(name = "end_date")
    private LocalDate endDate;
    
    private String instructions;
    
    private boolean active = true;
    

    private boolean reminderEnabled = false;
    
    @Column(name = "reminder_time")
    private LocalTime reminderTime;
    
    @Enumerated(EnumType.STRING)
    private ReminderFrequency reminderFrequency = ReminderFrequency.DAILY;
    

    public enum ReminderFrequency {
        DAILY, WEEKLY, MONTHLY, AS_NEEDED
    }
    

    public Medication() {}
    
    public Medication(User patient, String medicationName, String dosage, String frequency) {
        this.patient = patient;
        this.medicationName = medicationName;
        this.dosage = dosage;
        this.frequency = frequency;
    }
    

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public User getPatient() { return patient; }
    public void setPatient(User patient) { this.patient = patient; }
    
    public String getMedicationName() { return medicationName; }
    public void setMedicationName(String medicationName) { this.medicationName = medicationName; }
    
    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }
    
    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }
    
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    
    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }
    
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    
    public boolean isReminderEnabled() { return reminderEnabled; }
    public void setReminderEnabled(boolean reminderEnabled) { this.reminderEnabled = reminderEnabled; }
    
    public LocalTime getReminderTime() { return reminderTime; }
    public void setReminderTime(LocalTime reminderTime) { this.reminderTime = reminderTime; }
    
    public ReminderFrequency getReminderFrequency() { return reminderFrequency; }
    public void setReminderFrequency(ReminderFrequency reminderFrequency) { this.reminderFrequency = reminderFrequency; }
}