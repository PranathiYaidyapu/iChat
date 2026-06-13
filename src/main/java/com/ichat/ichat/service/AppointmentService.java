package com.ichat.ichat.service;

import com.ichat.ichat.model.Appointment;
import com.ichat.ichat.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentService {


    Appointment save(Appointment appointment);

    void cancelAppointment(Long appointmentId);


    List<Appointment> getUpcomingAppointments(User patient);


    List<Appointment> getPastAppointments(User patient);


    List<Appointment> getAllAppointments(User patient);

    Appointment bookAppointment(User patient, Long doctorId, LocalDateTime start, String reason);

    void sendAppointmentReminders();
    List<Appointment> getAppointmentsDueForReminder();
    List<Appointment> getAppointmentsByDateRange(User patient, LocalDateTime start, LocalDateTime end);
}
