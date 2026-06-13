package com.ichat.ichat.service;

import com.ichat.ichat.model.MedicalHistory;
import com.ichat.ichat.model.User;
import java.util.Optional;

public interface MedicalHistoryService {
    MedicalHistory saveMedicalHistory(MedicalHistory medicalHistory);
    Optional<MedicalHistory> getMedicalHistoryByUser(User user);
    MedicalHistory createOrUpdateMedicalHistory(User user, MedicalHistory medicalHistoryData);
    Optional<MedicalHistory> findById(Long id);
}