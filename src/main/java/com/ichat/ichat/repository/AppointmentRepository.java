package com.ichat.ichat.repository;

import com.ichat.ichat.model.Appointment;
import com.ichat.ichat.model.Doctor;
import com.ichat.ichat.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatientOrderByStartTimeDesc(User patient);
    List<Appointment> findByDoctorOrderByStartTimeDesc(Doctor doctor);
    List<Appointment> findByDoctorAndStartTimeBetween(Doctor doctor, LocalDateTime start, LocalDateTime end);
    List<Appointment> findByPatientAndStatus(User patient, String status);
    List<Appointment> findByPatientOrderByStartTime(User patient);
    boolean existsByDoctorAndStartTime(Doctor doctor, LocalDateTime startTime);
    List<Appointment> findByPatientAndAppointmentDateGreaterThanEqualAndStatusOrderByAppointmentDateAsc(
            User patient, LocalDate date, Appointment.Status status);
    List<Appointment> findByPatientAndStatusOrderByStartTime(User patient, Appointment.Status status);

    List<Appointment> findByPatientAndAppointmentDateBeforeOrderByAppointmentDateDesc(User patient, LocalDate date);


    List<Appointment> findByPatientOrderByAppointmentDateDesc(User patient);
    @Query("SELECT a FROM Appointment a WHERE a.doctor = :doctor AND " +
            "((:start BETWEEN a.startTime AND a.endTime) OR (:end BETWEEN a.startTime AND a.endTime) " +
            "OR (a.startTime BETWEEN :start AND :end)) " +
            "AND a.status <> 'CANCELLED'")
    List<Appointment> findConflictingDoctorAppointments(@Param("doctor") Doctor doctor,
                                                        @Param("start") LocalDateTime start,
                                                        @Param("end") LocalDateTime end);

    @Query("SELECT a FROM Appointment a WHERE a.patient = :patient AND " +
            "((:start BETWEEN a.startTime AND a.endTime) OR (:end BETWEEN a.startTime AND a.endTime) " +
            "OR (a.startTime BETWEEN :start AND :end)) " +
            "AND a.status <> 'CANCELLED'")
    List<Appointment> findConflictingPatientAppointments(@Param("patient") User patient,
                                                         @Param("start") LocalDateTime start,
                                                         @Param("end") LocalDateTime end);

    @Query("SELECT a FROM Appointment a WHERE a.startTime BETWEEN :start AND :end AND a.status = :status")
    List<Appointment> findByStartTimeBetweenAndStatus(@Param("start") LocalDateTime start,
                                                      @Param("end") LocalDateTime end,
                                                      @Param("status") Appointment.Status status);

    List<Appointment> findByPatientAndStartTimeBetweenOrderByStartTime(User patient, LocalDateTime start, LocalDateTime end);
    @Query("SELECT a FROM Appointment a WHERE a.patient = :patient " +
            "AND a.status = 'SCHEDULED' " +
            "AND a.appointmentDate >= :today " +
            "ORDER BY a.startTime ASC")
    List<Appointment> findUpcomingAppointments(@Param("patient") User patient,
                                               @Param("today") LocalDate today);


    @Query("SELECT a FROM Appointment a WHERE a.patient = :patient " +
            "AND a.status = 'SCHEDULED' " +
            "AND a.appointmentDate < :today " +
            "ORDER BY a.startTime DESC")
    List<Appointment> findPastAppointments(@Param("patient") User patient,
                                           @Param("today") LocalDate today);

    List<Appointment> findByDoctor(Doctor doctor);

    List<Appointment> findByDoctorAndStatusOrderByStartTimeAsc(Doctor doctor, Appointment.Status status);

    List<Appointment> findByDoctorAndStartTimeAfterAndStatusOrderByStartTimeAsc(
            Doctor doctor, LocalDateTime startTime, Appointment.Status status);

    List<Appointment> findByDoctorAndStartTimeBeforeOrderByStartTimeDesc(
            Doctor doctor, LocalDateTime startTime);

    List<Appointment> findByDoctorAndAppointmentDateAndStatusOrderByStartTimeAsc(
            Doctor doctor, LocalDate date, Appointment.Status status);

    @Query("SELECT a FROM Appointment a WHERE a.doctor = :doctor " +
            "AND LOWER(CONCAT(a.patient.firstname, ' ', a.patient.lastName)) LIKE LOWER(CONCAT('%', :patientName, '%')) " +
            "ORDER BY a.startTime ASC")
    List<Appointment> findByDoctorAndPatientNameContainingIgnoreCase(
            @Param("doctor") Doctor doctor,
            @Param("patientName") String patientName);



}
