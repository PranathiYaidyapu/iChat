package com.ichat.ichat.service;

import com.ichat.ichat.model.Doctor;
import java.util.List;
import java.util.Optional;

public interface DoctorService {
    List<Doctor> getAllDoctors();
    Optional<Doctor> getDoctorById(Long id);
    Doctor saveDoctor(Doctor doctor);
}
