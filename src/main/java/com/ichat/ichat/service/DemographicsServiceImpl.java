package com.ichat.ichat.service;

import com.ichat.ichat.model.Demographics;
import com.ichat.ichat.model.User;
import com.ichat.ichat.repository.DemographicsRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class DemographicsServiceImpl implements DemographicsService {
    
    private final DemographicsRepository demographicsRepository;
    
    public DemographicsServiceImpl(DemographicsRepository demographicsRepository) {
        this.demographicsRepository = demographicsRepository;
    }
    
    @Override
    public Demographics saveDemographics(Demographics demographics) {
        return demographicsRepository.save(demographics);
    }
    
    @Override
    public Optional<Demographics> getDemographicsByUser(User user) {
        return demographicsRepository.findByUser(user);
    }
    
    @Override
    public Demographics createOrUpdateDemographics(User user, Demographics demographicsData) {
        Optional<Demographics> existing = demographicsRepository.findByUser(user);
        
        if (existing.isPresent()) {
            Demographics current = existing.get();

            current.setDateOfBirth(demographicsData.getDateOfBirth());
            current.setGender(demographicsData.getGender());
            current.setAddress(demographicsData.getAddress());
            current.setCity(demographicsData.getCity());
            current.setState(demographicsData.getState());
            current.setZipCode(demographicsData.getZipCode());
            current.setPhoneNumber(demographicsData.getPhoneNumber());
            current.setEthnicity(demographicsData.getEthnicity());
            current.setEmergencyContactName(demographicsData.getEmergencyContactName());
            current.setEmergencyContactPhone(demographicsData.getEmergencyContactPhone());
            current.setEmergencyContactRelationship(demographicsData.getEmergencyContactRelationship());
            
            return demographicsRepository.save(current);
        } else {

            demographicsData.setUser(user);
            return demographicsRepository.save(demographicsData);
        }
    }
}