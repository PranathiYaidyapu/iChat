package com.ichat.ichat.service;

import com.ichat.ichat.model.MedicalHistory;
import com.ichat.ichat.model.User;
import com.ichat.ichat.repository.MedicalHistoryRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class MedicalHistoryServiceImpl implements MedicalHistoryService {
    
    private final MedicalHistoryRepository medicalHistoryRepository;
    
    public MedicalHistoryServiceImpl(MedicalHistoryRepository medicalHistoryRepository) {
        this.medicalHistoryRepository = medicalHistoryRepository;
    }
    
    @Override
    public MedicalHistory saveMedicalHistory(MedicalHistory medicalHistory) {
        return medicalHistoryRepository.save(medicalHistory);
    }
    
    @Override
    public Optional<MedicalHistory> getMedicalHistoryByUser(User user) {
        return medicalHistoryRepository.findByUser(user);
    }

    @Override
    public MedicalHistory createOrUpdateMedicalHistory(User user, MedicalHistory medicalHistoryData) {
        Optional<MedicalHistory> existing = medicalHistoryRepository.findByUser(user);

        if (existing.isPresent()) {
            MedicalHistory current = existing.get();

            current.setConditions(medicalHistoryData.getConditions() != null ?
                    medicalHistoryData.getConditions() : new ArrayList<>());
            current.setCurrentMedications(medicalHistoryData.getCurrentMedications() != null ?
                    medicalHistoryData.getCurrentMedications() : new ArrayList<>());
            current.setAllergies(medicalHistoryData.getAllergies() != null ?
                    medicalHistoryData.getAllergies() : new ArrayList<>());


            return medicalHistoryRepository.save(current);
        } else {

            if (medicalHistoryData.getConditions() == null) {
                medicalHistoryData.setConditions(new ArrayList<>());
            }
            if (medicalHistoryData.getCurrentMedications() == null) {
                medicalHistoryData.setCurrentMedications(new ArrayList<>());
            }
            if (medicalHistoryData.getAllergies() == null) {
                medicalHistoryData.setAllergies(new ArrayList<>());
            }
            medicalHistoryData.setUser(user);
            return medicalHistoryRepository.save(medicalHistoryData);
        }
    }

    @Override
    public Optional<MedicalHistory> findById(Long id) {
        return medicalHistoryRepository.findByUserId(id);
    }
}