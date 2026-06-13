package com.ichat.ichat.repository;

import com.ichat.ichat.model.Medication;
import com.ichat.ichat.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface MedicationRepository extends JpaRepository<Medication, Long> {
    List<Medication> findByPatientOrderByMedicationName(User patient);
    List<Medication> findByPatientAndActiveTrueOrderByMedicationName(User patient);
    List<Medication> findByReminderEnabledTrueAndActiveTrue();

    @Query("SELECT m FROM Medication m WHERE m.reminderEnabled = true AND m.active = true " +
            "AND m.reminderTime = :currentTime")
    List<Medication> findMedicationsForReminder(@Param("currentTime") LocalTime currentTime);
    
    List<Medication> findByPatientAndEndDateBeforeAndActiveTrue(User patient, LocalDate date);
    @Query("""
       SELECT m 
       FROM Medication m
       WHERE m.reminderEnabled = true
         AND m.active = true
         AND m.reminderTime > :now
         AND m.reminderTime <= :nowPlus
       """)
    List<Medication> findUpcomingMedications(
            @Param("now") LocalTime now,
            @Param("nowPlus") LocalTime nowPlus);

}