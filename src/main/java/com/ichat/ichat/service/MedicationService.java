package com.ichat.ichat.service;

import com.ichat.ichat.model.Medication;
import com.ichat.ichat.model.User;

import java.util.List;

public interface MedicationService {
    Medication saveMedication(Medication medication);
    List<Medication> getMedicationsByPatient(User patient);
    List<Medication> getActiveMedicationsByPatient(User patient);
    Medication getMedicationById(Long id);
    void deleteMedication(Long id);
    Medication updateMedication(Long id, Medication medication);
    List<Medication> getMedicationsDueForReminder();
    void sendMedicationReminders();
}