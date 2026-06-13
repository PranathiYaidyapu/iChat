package com.ichat.ichat.service;

import com.ichat.ichat.model.Appointment;
import com.ichat.ichat.model.Doctor;
import com.ichat.ichat.model.User;
import com.ichat.ichat.repository.AppointmentRepository;
import com.ichat.ichat.repository.DoctorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Transactional
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final EmailService emailService;

    public AppointmentServiceImpl(AppointmentRepository appointmentRepository, DoctorRepository doctorRepository, EmailService emailService) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.emailService = emailService;
    }

    @Override
    public Appointment save(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }

    @Override
    public void cancelAppointment(Long appointmentId) {
        appointmentRepository.findById(appointmentId).ifPresent(appointment -> {
            appointment.setStatus(Appointment.Status.CANCELLED);
            appointmentRepository.save(appointment);
        });
    }

    @Override
    public List<Appointment> getUpcomingAppointments(User patient) {
        LocalDate today = LocalDate.now();
        return appointmentRepository
                .findByPatientAndAppointmentDateGreaterThanEqualAndStatusOrderByAppointmentDateAsc(
                        patient, today, Appointment.Status.SCHEDULED);
    }

    @Override
    public List<Appointment> getPastAppointments(User patient) {
        LocalDate today = LocalDate.now();
        return appointmentRepository
                .findByPatientAndAppointmentDateBeforeOrderByAppointmentDateDesc(patient, today);
    }

    @Override
    public List<Appointment> getAllAppointments(User patient) {
        return appointmentRepository.findByPatientOrderByAppointmentDateDesc(patient);
    }
    @Override
    public Appointment bookAppointment(User patient, Long doctorId, LocalDateTime start, String reason) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        LocalDateTime end = start.plusMinutes(30);


        boolean doctorBusy = !appointmentRepository
                .findConflictingDoctorAppointments(doctor, start, end).isEmpty();
        if (doctorBusy) {
            throw new RuntimeException("Doctor is not available at this time slot.");
        }


        boolean patientBusy = !appointmentRepository
                .findConflictingPatientAppointments(patient, start, end).isEmpty();
        if (patientBusy) {
            throw new RuntimeException("You already have an appointment at this time.");
        }

        Appointment appointment = new Appointment();
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setStartTime(start);
        appointment.setEndTime(end);
        appointment.setReason(reason);
        appointment.setStatus(Appointment.Status.SCHEDULED);

        return appointmentRepository.save(appointment);
    }
    @Override
    public List<Appointment> getAppointmentsDueForReminder() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime reminderStart = now.plusHours(0);
        LocalDateTime reminderEnd = now.plusHours(12);
        System.out.println("printing time"+reminderStart+" "+reminderEnd);
        return appointmentRepository.findByStartTimeBetweenAndStatus(
                reminderStart, reminderEnd, Appointment.Status.SCHEDULED);
    }

    @Override
    public List<Appointment> getAppointmentsByDateRange(User patient, LocalDateTime start, LocalDateTime end) {
        return appointmentRepository.findByPatientAndStartTimeBetweenOrderByStartTime(patient, start, end);
    }
    @Override
    @Scheduled(cron = "0 0 7 * * *")
    public void sendAppointmentReminders() {
        List<Appointment> appointments = getAppointmentsDueForReminder();
       System.out.println("Appointment Job Triggered with size"+appointments.size());
        System.out.println(appointmentRepository.findAll());
        for (Appointment appointment : appointments) {
            User patient = appointment.getPatient();
            Doctor doctor = appointment.getDoctor();

            emailService.sendAppointmentReminder(
                    patient.getUsername(),
                    patient.getFirstname() + " " + patient.getLastName(),
                    doctor.getUser().getFirstname() + " " + doctor.getUser().getLastName(),
                    appointment.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                    appointment.getReason()
            );

            // Log the reminder
            System.out.println("Appointment reminder sent for: " +
                    appointment.getStartTime() + " to " + patient.getUsername());
        }
    }

}




