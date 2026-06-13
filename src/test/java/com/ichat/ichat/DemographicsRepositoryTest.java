package com.ichat.ichat;

import com.ichat.ichat.model.Demographics;
import com.ichat.ichat.model.User;
import com.ichat.ichat.repository.DemographicsRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class DemographicsRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DemographicsRepository demographicsRepository;

    private User createValidUser() {
        User user = new User();
        user.setUsername("test@example.com");
        user.setPassword("password123");
        user.setFirstname("John");
        user.setLastName("Doe");
        return user;
    }

    @Test
    void findByUser_WhenUserExists_ShouldReturnDemographics() {
        // Arrange
        User user = createValidUser();
        user = entityManager.persistAndFlush(user);

        Demographics demographics = new Demographics();
        demographics.setUser(user);
        demographics = entityManager.persistAndFlush(demographics);


        Optional<Demographics> result = demographicsRepository.findByUser(user);


        assertTrue(result.isPresent());
        assertEquals(demographics.getId(), result.get().getId());
        assertEquals(user.getId(), result.get().getUser().getId());
    }

    @Test
    void findByUserId_WhenUserExists_ShouldReturnDemographics() {

        User user = createValidUser();
        user = entityManager.persistAndFlush(user);

        Demographics demographics = new Demographics();
        demographics.setUser(user);
        entityManager.persistAndFlush(demographics);


        Optional<Demographics> result = demographicsRepository.findByUserId(user.getId());


        assertTrue(result.isPresent());
        assertEquals(user.getId(), result.get().getUser().getId());
    }

    @Test
    void findByUser_WhenNotExists_ShouldReturnEmpty() {

        User user = createValidUser();
        user = entityManager.persistAndFlush(user);


        Optional<Demographics> result = demographicsRepository.findByUser(user);


        assertFalse(result.isPresent());
    }
}