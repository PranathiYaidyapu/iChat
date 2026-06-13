package com.ichat.ichat.repository;

import com.ichat.ichat.model.MedicalHistory;
import com.ichat.ichat.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MedicalHistoryRepository extends JpaRepository<MedicalHistory, Long> {
    Optional<MedicalHistory> findByUser(User user);
    Optional<MedicalHistory> findByUserId(Long userId);
}