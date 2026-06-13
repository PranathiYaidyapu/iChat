package com.ichat.ichat.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "medical_history")
public class MedicalHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "medical_conditions", joinColumns = @JoinColumn(name = "medical_history_id"))
    @Column(name = "`condition`")
    private List<String> conditions = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "current_medications", joinColumns = @JoinColumn(name = "medical_history_id"))
    @Column(name = "medication")
    private List<String> currentMedications = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "allergies", joinColumns = @JoinColumn(name = "medical_history_id"))
    @Column(name = "allergy")
    private List<String> allergies = new ArrayList<>();
    
    @Column(name = "family_history", length = 1000)
    private String familyHistory;
    
    @Column(name = "previous_treatments", length = 1000)
    private String previousTreatments;
    
    @Column(name = "current_therapist")
    private String currentTherapist;
    
    @Column(name = "therapist_contact")
    private String therapistContact;
    
    @Column(name = "primary_care_physician")
    private String primaryCarePhysician;
    
    @Column(name = "physician_contact")
    private String physicianContact;
    
    @Column(name = "insurance_provider")
    private String insuranceProvider;
    
    @Column(name = "insurance_id")
    private String insuranceId;
    
    @Column(name = "last_physical_exam")
    private LocalDate lastPhysicalExam;
    
    // Mental Health Specific Fields
    @Column(name = "previous_diagnosis", length = 1000)
    private String previousDiagnosis;
    
    @Column(name = "hospitalization_history", length = 1000)
    private String hospitalizationHistory;
    
    @Column(name = "substance_use_history", length = 500)
    private String substanceUseHistory;
    
    @Column(name = "trauma_history", length = 1000)
    private String traumaHistory;
    

    public MedicalHistory() {}
    
    public MedicalHistory(User user) {
        this.user = user;
    }
    

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public List<String> getConditions() { return conditions; }
    public void setConditions(List<String> conditions) { this.conditions = conditions; }
    
    public List<String> getCurrentMedications() { return currentMedications; }
    public void setCurrentMedications(List<String> currentMedications) { this.currentMedications = currentMedications; }
    
    public List<String> getAllergies() { return allergies; }
    public void setAllergies(List<String> allergies) { this.allergies = allergies; }
    
    public String getFamilyHistory() { return familyHistory; }
    public void setFamilyHistory(String familyHistory) { this.familyHistory = familyHistory; }
    
    public String getPreviousTreatments() { return previousTreatments; }
    public void setPreviousTreatments(String previousTreatments) { this.previousTreatments = previousTreatments; }
    
    public String getCurrentTherapist() { return currentTherapist; }
    public void setCurrentTherapist(String currentTherapist) { this.currentTherapist = currentTherapist; }
    
    public String getTherapistContact() { return therapistContact; }
    public void setTherapistContact(String therapistContact) { this.therapistContact = therapistContact; }
    
    public String getPrimaryCarePhysician() { return primaryCarePhysician; }
    public void setPrimaryCarePhysician(String primaryCarePhysician) { this.primaryCarePhysician = primaryCarePhysician; }
    
    public String getPhysicianContact() { return physicianContact; }
    public void setPhysicianContact(String physicianContact) { this.physicianContact = physicianContact; }
    
    public String getInsuranceProvider() { return insuranceProvider; }
    public void setInsuranceProvider(String insuranceProvider) { this.insuranceProvider = insuranceProvider; }
    
    public String getInsuranceId() { return insuranceId; }
    public void setInsuranceId(String insuranceId) { this.insuranceId = insuranceId; }
    
    public LocalDate getLastPhysicalExam() { return lastPhysicalExam; }
    public void setLastPhysicalExam(LocalDate lastPhysicalExam) { this.lastPhysicalExam = lastPhysicalExam; }
    
    public String getPreviousDiagnosis() { return previousDiagnosis; }
    public void setPreviousDiagnosis(String previousDiagnosis) { this.previousDiagnosis = previousDiagnosis; }
    
    public String getHospitalizationHistory() { return hospitalizationHistory; }
    public void setHospitalizationHistory(String hospitalizationHistory) { this.hospitalizationHistory = hospitalizationHistory; }
    
    public String getSubstanceUseHistory() { return substanceUseHistory; }
    public void setSubstanceUseHistory(String substanceUseHistory) { this.substanceUseHistory = substanceUseHistory; }
    
    public String getTraumaHistory() { return traumaHistory; }
    public void setTraumaHistory(String traumaHistory) { this.traumaHistory = traumaHistory; }
}