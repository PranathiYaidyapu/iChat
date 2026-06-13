package com.ichat.ichat.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ichat_appointments")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;


    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;


    private LocalDateTime startTime;
    private LocalDateTime endTime;


    private LocalDate appointmentDate;

    private String reason;

    @Enumerated(EnumType.STRING)
    private Status status = Status.SCHEDULED;


    public enum Status {
        SCHEDULED,
        COMPLETED,
        CANCELLED
    }


    public Appointment() {}

    public Appointment(User patient, Doctor doctor, LocalDateTime startTime, LocalDateTime endTime, String reason) {
        this.patient = patient;
        this.doctor = doctor;
        this.startTime = startTime;
        this.endTime = endTime;
        this.appointmentDate = startTime.toLocalDate();
        this.reason = reason;
        this.status = Status.SCHEDULED;
    }



    public Long getId() {
        return id;
    }

    public User getPatient() {
        return patient;
    }

    public void setPatient(User patient) {
        this.patient = patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
        this.appointmentDate = startTime != null ? startTime.toLocalDate() : null;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
    @Override
    public String toString(){
        return "Appintment"+startTime+" "+endTime;
    }
}
