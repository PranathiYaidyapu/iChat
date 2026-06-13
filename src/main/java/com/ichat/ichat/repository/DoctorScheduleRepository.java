package com.ichat.ichat.repository;

import com.ichat.ichat.model.Doctor;
import com.ichat.ichat.model.DoctorSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, Long> {
    List<DoctorSchedule> findByDoctor(Optional<Doctor> doctor);
    List<DoctorSchedule> findByDoctorAndDayOfWeek(Doctor doctor, DayOfWeek dayOfWeek);
}
