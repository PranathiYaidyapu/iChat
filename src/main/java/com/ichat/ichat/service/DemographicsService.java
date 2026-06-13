package com.ichat.ichat.service;

import com.ichat.ichat.model.Demographics;
import com.ichat.ichat.model.User;
import java.util.Optional;

public interface DemographicsService {
    Demographics saveDemographics(Demographics demographics);
    Optional<Demographics> getDemographicsByUser(User user);
    Demographics createOrUpdateDemographics(User user, Demographics demographicsData);
}