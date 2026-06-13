package com.ichat.ichat;

import com.ichat.ichat.model.MedicalHistory;
import com.ichat.ichat.model.User;
import com.ichat.ichat.repository.MedicalHistoryRepository;
import com.ichat.ichat.service.MedicalHistoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class MedicalHistoryServiceTest {

    @Mock
    private MedicalHistoryRepository medicalHistoryRepository;

    @InjectMocks
    private MedicalHistoryServiceImpl medicalHistoryService;

    private User testUser;
    private MedicalHistory testMedicalHistory;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("test@example.com");
        testUser.setPassword("password123");
        testUser.setFirstname("John");
        testUser.setLastName("Doe");

        testMedicalHistory = new MedicalHistory();
        testMedicalHistory.setId(1L);
        testMedicalHistory.setUser(testUser);
        testMedicalHistory.setConditions(Arrays.asList("Anxiety", "Depression"));
        testMedicalHistory.setCurrentMedications(Arrays.asList("Sertraline 50mg"));
        testMedicalHistory.setAllergies(Arrays.asList("Penicillin"));
        testMedicalHistory.setFamilyHistory("Mother had depression");
        testMedicalHistory.setPreviousDiagnosis("Generalized Anxiety Disorder");
    }
    @Test
    void saveMedicalHistory_ShouldSaveMedicalHistory() {

        when(medicalHistoryRepository.save(any(MedicalHistory.class))).thenReturn(testMedicalHistory);


        MedicalHistory result = medicalHistoryService.saveMedicalHistory(testMedicalHistory);


        assertNotNull(result);
        assertEquals(testMedicalHistory.getId(), result.getId());
        assertEquals(2, result.getConditions().size());
        verify(medicalHistoryRepository, times(1)).save(testMedicalHistory);
    }

    @Test
    void getMedicalHistoryByUser_WhenExists_ShouldReturnMedicalHistory() {

        when(medicalHistoryRepository.findByUser(testUser)).thenReturn(Optional.of(testMedicalHistory));


        Optional<MedicalHistory> result = medicalHistoryService.getMedicalHistoryByUser(testUser);


        assertTrue(result.isPresent());
        assertEquals(testMedicalHistory, result.get());
        verify(medicalHistoryRepository, times(1)).findByUser(testUser);
    }

    @Test
    void getMedicalHistoryByUser_WhenNotExists_ShouldReturnEmpty() {

        when(medicalHistoryRepository.findByUser(testUser)).thenReturn(Optional.empty());


        Optional<MedicalHistory> result = medicalHistoryService.getMedicalHistoryByUser(testUser);


        assertFalse(result.isPresent());
        verify(medicalHistoryRepository, times(1)).findByUser(testUser);
    }

    @Test
    void createOrUpdateMedicalHistory_WhenNew_ShouldCreateMedicalHistory() {

        when(medicalHistoryRepository.findByUser(testUser)).thenReturn(Optional.empty());
        when(medicalHistoryRepository.save(any(MedicalHistory.class))).thenReturn(testMedicalHistory);


        MedicalHistory result = medicalHistoryService.createOrUpdateMedicalHistory(testUser, testMedicalHistory);


        assertNotNull(result);
        assertEquals(testUser, result.getUser());
        verify(medicalHistoryRepository, times(1)).findByUser(testUser);
        verify(medicalHistoryRepository, times(1)).save(testMedicalHistory);
    }

    @Test
    void createOrUpdateMedicalHistory_WhenExists_ShouldUpdateMedicalHistory() {

        MedicalHistory existingHistory = new MedicalHistory(testUser);
        existingHistory.setId(1L);
        existingHistory.setConditions(Arrays.asList("Old Condition"));

        MedicalHistory updatedData = new MedicalHistory();
        updatedData.setConditions(Arrays.asList("New Condition", "Another Condition"));
        updatedData.setFamilyHistory("Updated family history");

        when(medicalHistoryRepository.findByUser(testUser)).thenReturn(Optional.of(existingHistory));
        when(medicalHistoryRepository.save(any(MedicalHistory.class))).thenReturn(existingHistory);


        MedicalHistory result = medicalHistoryService.createOrUpdateMedicalHistory(testUser, updatedData);


        assertNotNull(result);
        assertEquals(2, result.getConditions().size());
        assertTrue(result.getConditions().contains("New Condition"));
        //assertEquals("Updated family history", result.getFamilyHistory());
        verify(medicalHistoryRepository, times(1)).findByUser(testUser);
        verify(medicalHistoryRepository, times(1)).save(existingHistory);
    }

    @Test
    void createOrUpdateMedicalHistory_ShouldHandleEmptyCollections() {

        MedicalHistory medicalHistoryWithNullCollections = new MedicalHistory();
        medicalHistoryWithNullCollections.setConditions(null);
        medicalHistoryWithNullCollections.setCurrentMedications(null);
        medicalHistoryWithNullCollections.setAllergies(null);

        when(medicalHistoryRepository.findByUser(testUser)).thenReturn(Optional.empty());
        when(medicalHistoryRepository.save(any(MedicalHistory.class))).thenReturn(medicalHistoryWithNullCollections);


        MedicalHistory result = medicalHistoryService.createOrUpdateMedicalHistory(testUser, medicalHistoryWithNullCollections);


        assertNotNull(result);

        verify(medicalHistoryRepository, times(1)).save(any(MedicalHistory.class));
    }

    @Test
    void createOrUpdateMedicalHistory_ShouldMaintainUserRelationship() {

        when(medicalHistoryRepository.findByUser(testUser)).thenReturn(Optional.empty());
        when(medicalHistoryRepository.save(any(MedicalHistory.class))).thenAnswer(invocation -> {
            MedicalHistory saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });


        MedicalHistory result = medicalHistoryService.createOrUpdateMedicalHistory(testUser, testMedicalHistory);


        assertEquals(testUser, result.getUser());
        assertNotNull(result.getId());
    }
}