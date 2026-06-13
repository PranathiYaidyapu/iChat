package com.ichat.ichat.repository;

import com.ichat.ichat.model.Demographics;
import com.ichat.ichat.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface DemographicsRepository extends JpaRepository<Demographics, Long> {
    Optional<Demographics> findByUser(User user);
    Optional<Demographics> findByUserId(Long userId);
}