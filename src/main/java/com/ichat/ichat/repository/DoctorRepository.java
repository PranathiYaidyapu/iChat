package com.ichat.ichat.repository;

import com.ichat.ichat.model.Doctor;
import com.ichat.ichat.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByUserId(Long userId);
    Optional<Doctor> findByUser(User user);
}
