package com.ichat.ichat.service;

import com.ichat.ichat.model.Medication;
import com.ichat.ichat.model.User;
import com.ichat.ichat.repository.MedicationRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Service
public class MedicationServiceImpl implements MedicationService {
    
    private final MedicationRepository medicationRepository;
    private final EmailService emailService;
    
    public MedicationServiceImpl(MedicationRepository medicationRepository, EmailService emailService) {
        this.medicationRepository = medicationRepository;
        this.emailService = emailService;
    }
    
    @Override
    public Medication saveMedication(Medication medication) {
        return medicationRepository.save(medication);
    }
    
    @Override
    public List<Medication> getMedicationsByPatient(User patient) {
        return medicationRepository.findByPatientOrderByMedicationName(patient);
    }
    
    @Override
    public List<Medication> getActiveMedicationsByPatient(User patient) {
        return medicationRepository.findByPatientAndActiveTrueOrderByMedicationName(patient);
    }
    
    @Override
    public Medication getMedicationById(Long id) {
        return medicationRepository.findById(id).orElse(null);
    }
    
    @Override
    public void deleteMedication(Long id) {
        medicationRepository.deleteById(id);
    }
    
    @Override
    public Medication updateMedication(Long id, Medication medication) {
        Medication existing = getMedicationById(id);
        if (existing != null) {
            existing.setMedicationName(medication.getMedicationName());
            existing.setDosage(medication.getDosage());
            existing.setFrequency(medication.getFrequency());
            existing.setStartDate(medication.getStartDate());
            existing.setEndDate(medication.getEndDate());
            existing.setInstructions(medication.getInstructions());
            existing.setActive(medication.isActive());
            existing.setReminderEnabled(medication.isReminderEnabled());
            existing.setReminderTime(medication.getReminderTime());
            existing.setReminderFrequency(medication.getReminderFrequency());
            return medicationRepository.save(existing);
        }
        return null;
    }
    
    @Override
    public List<Medication> getMedicationsDueForReminder() {
        LocalTime currentTime = LocalTime.now().withSecond(0).withNano(0);
        return medicationRepository.findMedicationsForReminder(currentTime);
    }

    @Override
    @Scheduled(cron = "0 0/30 * * * ?")
    public void sendMedicationReminders() {
        //LocalTime currentTime = LocalTime.now().withSecond(0).withNano(0).plusMinutes(30);
        //List<Medication> medications = medicationRepository.findMedicationsForReminder(currentTime);
        LocalTime now = LocalTime.now().minusMinutes(1);
        LocalTime nowPlus = now.plusMinutes(1);
        List<Medication> medications = medicationRepository.findUpcomingMedications(now, nowPlus);
        System.out.println("Appointment Job Triggered with size"+medications.size());

        for (Medication medication : medications) {
            User patient = medication.getPatient();
            emailService.sendMedicationReminder(
                    patient.getUsername(),
                    patient.getFirstname() + " " + patient.getLastName(),
                    medication.getMedicationName(),
                    medication.getDosage(),
                    medication.getInstructions()
            );

            // Optional: Log that reminder was sent
            System.out.println("Medication reminder sent for: " + medication.getMedicationName() +
                    " at " + now + " to " + patient.getUsername());
        }
    }
}