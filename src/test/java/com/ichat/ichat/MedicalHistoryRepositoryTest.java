package com.ichat.ichat;

import com.ichat.ichat.model.MedicalHistory;
import com.ichat.ichat.model.User;
import com.ichat.ichat.repository.MedicalHistoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class MedicalHistoryRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MedicalHistoryRepository medicalHistoryRepository;

    private User createValidUser() {
        User user = new User();
        user.setUsername("medical@test.com");
        user.setPassword("password123");
        user.setFirstname("Jane");
        user.setLastName("Smith");
        return user;
    }

    @Test
    void findByUser_WhenExists_ShouldReturnMedicalHistory() {

        User user = createValidUser();
        user = entityManager.persistAndFlush(user);

        MedicalHistory medicalHistory = new MedicalHistory();
        medicalHistory.setUser(user);
        medicalHistory.setConditions(Arrays.asList("Anxiety", "Depression"));
        medicalHistory.setCurrentMedications(Arrays.asList("Medication A"));
        medicalHistory = entityManager.persistAndFlush(medicalHistory);


        Optional<MedicalHistory> result = medicalHistoryRepository.findByUser(user);


        assertTrue(result.isPresent());
        assertEquals(medicalHistory.getId(), result.get().getId());
        assertEquals(2, result.get().getConditions().size());
    }

    @Test
    void findByUserId_WhenExists_ShouldReturnMedicalHistory() {

        User user = createValidUser();
        user = entityManager.persistAndFlush(user);

        MedicalHistory medicalHistory = new MedicalHistory();
        medicalHistory.setUser(user);
        medicalHistory.setAllergies(Arrays.asList("Peanuts"));
        entityManager.persistAndFlush(medicalHistory);


        Optional<MedicalHistory> result = medicalHistoryRepository.findByUserId(user.getId());


        assertTrue(result.isPresent());
        assertEquals(user.getId(), result.get().getUser().getId());
        assertEquals(1, result.get().getAllergies().size());
    }
}