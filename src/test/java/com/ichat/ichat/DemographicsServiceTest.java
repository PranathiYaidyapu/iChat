package com.ichat.ichat;

import com.ichat.ichat.model.Demographics;
import com.ichat.ichat.model.User;
import com.ichat.ichat.repository.DemographicsRepository;
import com.ichat.ichat.service.DemographicsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class DemographicsServiceTest {

    @Mock
    private DemographicsRepository demographicsRepository;

    @InjectMocks
    private DemographicsServiceImpl demographicsService;

    private User testUser;
    private Demographics testDemographics;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("test@example.com");
        testUser.setPassword("password123");
        testUser.setFirstname("John");
        testUser.setLastName("Doe");

        testDemographics = new Demographics();
        testDemographics.setId(1L);
        testDemographics.setUser(testUser);
        testDemographics.setDateOfBirth(LocalDate.of(1990, 1, 1));
        testDemographics.setGender("Male");
        testDemographics.setPhoneNumber("123-456-7890");
    }

    @Test
    void saveDemographics_ShouldSaveNewDemographics() {

        when(demographicsRepository.save(any(Demographics.class))).thenReturn(testDemographics);


        Demographics result = demographicsService.saveDemographics(testDemographics);


        assertNotNull(result);
        assertEquals(testDemographics.getId(), result.getId());
        assertEquals(testUser, result.getUser());
        verify(demographicsRepository, times(1)).save(testDemographics);
    }

    @Test
    void getDemographicsByUser_WhenExists_ShouldReturnDemographics() {

        when(demographicsRepository.findByUser(testUser)).thenReturn(Optional.of(testDemographics));


        Optional<Demographics> result = demographicsService.getDemographicsByUser(testUser);


        assertTrue(result.isPresent());
        assertEquals(testDemographics, result.get());
        verify(demographicsRepository, times(1)).findByUser(testUser);
    }

    @Test
    void getDemographicsByUser_WhenNotExists_ShouldReturnEmpty() {

        when(demographicsRepository.findByUser(testUser)).thenReturn(Optional.empty());


        Optional<Demographics> result = demographicsService.getDemographicsByUser(testUser);


        assertFalse(result.isPresent());
        verify(demographicsRepository, times(1)).findByUser(testUser);
    }

    @Test
    void createOrUpdateDemographics_WhenNew_ShouldCreateDemographics() {

        when(demographicsRepository.findByUser(testUser)).thenReturn(Optional.empty());
        when(demographicsRepository.save(any(Demographics.class))).thenReturn(testDemographics);


        Demographics result = demographicsService.createOrUpdateDemographics(testUser, testDemographics);


        assertNotNull(result);
        assertEquals(testUser, result.getUser());
        verify(demographicsRepository, times(1)).findByUser(testUser);
        verify(demographicsRepository, times(1)).save(testDemographics);
    }

    @Test
    void createOrUpdateDemographics_WhenExists_ShouldUpdateDemographics() {

        Demographics existingDemographics = new Demographics(testUser);
        existingDemographics.setId(1L);
        existingDemographics.setPhoneNumber("old-phone");

        Demographics updatedData = new Demographics();
        updatedData.setPhoneNumber("new-phone");
        updatedData.setGender("Female");

        when(demographicsRepository.findByUser(testUser)).thenReturn(Optional.of(existingDemographics));
        when(demographicsRepository.save(any(Demographics.class))).thenReturn(existingDemographics);


        Demographics result = demographicsService.createOrUpdateDemographics(testUser, updatedData);


        assertNotNull(result);
        assertEquals("new-phone", result.getPhoneNumber());
        assertEquals("Female", result.getGender());
        verify(demographicsRepository, times(1)).findByUser(testUser);
        verify(demographicsRepository, times(1)).save(existingDemographics);
    }

    @Test
    void createOrUpdateDemographics_ShouldMaintainUserRelationship() {

        when(demographicsRepository.findByUser(testUser)).thenReturn(Optional.empty());
        when(demographicsRepository.save(any(Demographics.class))).thenAnswer(invocation -> {
            Demographics saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });


        Demographics result = demographicsService.createOrUpdateDemographics(testUser, testDemographics);


        assertEquals(testUser, result.getUser());
        assertNotNull(result.getId());
    }
}