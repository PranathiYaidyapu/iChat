package com.ichat.ichat.service;

import com.ichat.ichat.model.Doctor;
import com.ichat.ichat.model.DoctorSchedule;
import com.ichat.ichat.repository.AppointmentRepository;
import com.ichat.ichat.repository.DoctorScheduleRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DoctorScheduleService {

    private final DoctorScheduleRepository scheduleRepository;
    private final AppointmentRepository appointmentRepository;

    public DoctorScheduleService(DoctorScheduleRepository scheduleRepository, AppointmentRepository appointmentRepository) {
        this.scheduleRepository = scheduleRepository;
        this.appointmentRepository = appointmentRepository;
    }

    public List<DoctorSchedule> getSchedulesForDoctor(Optional<Doctor> doctor) {
        return scheduleRepository.findByDoctor(doctor);
    }

    public List<DoctorSchedule> getScheduleForDay(Doctor doctor, DayOfWeek day) {
        return scheduleRepository.findByDoctorAndDayOfWeek(doctor, day);
    }

    public DoctorSchedule saveSchedule(DoctorSchedule schedule) {
        return scheduleRepository.save(schedule);
    }

    public void deleteSchedule(Long id) {
        scheduleRepository.deleteById(id);
    }

    public List<LocalDateTime> getAvailableSlots(Doctor doctor, LocalDate date) {
        List<DoctorSchedule> schedules = scheduleRepository.findByDoctorAndDayOfWeek(doctor, date.getDayOfWeek());
        List<LocalDateTime> slots = new ArrayList<>();

        for (DoctorSchedule schedule : schedules) {
            LocalTime time = schedule.getStartTime();
            while (time.isBefore(schedule.getEndTime())) {
                if (schedule.getBreakStart() != null &&
                        !time.isBefore(schedule.getBreakStart()) &&
                        time.isBefore(schedule.getBreakEnd())) {
                    time = schedule.getBreakEnd();
                    continue;
                }

                LocalDateTime startSlot = LocalDateTime.of(date, time);
                LocalDateTime endSlot = startSlot.plusMinutes(30);

                boolean booked = appointmentRepository.existsByDoctorAndStartTime(doctor, startSlot);
                if (!booked) slots.add(startSlot);

                time = endSlot.toLocalTime();
            }
        }

        return slots;
    }

}
