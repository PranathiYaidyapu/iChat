package com.ichat.ichat.controller;

import com.ichat.ichat.model.Appointment;
import com.ichat.ichat.model.User;
import com.ichat.ichat.service.AppointmentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/patient/calendar")
public class CalendarController {

    private final AppointmentService appointmentService;

    public CalendarController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping
    public String viewCalendar(HttpSession session, Model model,
                               @RequestParam(required = false) Integer year,
                               @RequestParam(required = false) Integer month) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !user.getRoles().contains("ROLE_PATIENT")) {
            return "redirect:/login";
        }


        LocalDate currentDate = LocalDate.now();
        int currentYear = year != null ? year : currentDate.getYear();
        int currentMonth = month != null ? month : currentDate.getMonthValue();

        YearMonth yearMonth = YearMonth.of(currentYear, currentMonth);
        LocalDate firstOfMonth = yearMonth.atDay(1);
        LocalDate lastOfMonth = yearMonth.atEndOfMonth();


        LocalDate calendarStart = firstOfMonth.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        LocalDate calendarEnd = lastOfMonth.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));


        List<List<LocalDate>> calendarWeeks = new ArrayList<>();
        List<LocalDate> currentWeek = new ArrayList<>();

        LocalDate currentDay = calendarStart;
        while (!currentDay.isAfter(calendarEnd)) {
            currentWeek.add(currentDay);


            if (currentWeek.size() == 7) {
                calendarWeeks.add(currentWeek);
                currentWeek = new ArrayList<>();
            }

            currentDay = currentDay.plusDays(1);
        }


        List<Appointment> monthlyAppointments = appointmentService.getAppointmentsByDateRange(
                user, firstOfMonth.atStartOfDay(), lastOfMonth.plusDays(1).atStartOfDay());

        model.addAttribute("user", user);
        model.addAttribute("currentYear", currentYear);
        model.addAttribute("currentMonth", currentMonth);
        model.addAttribute("yearMonth", yearMonth);
        model.addAttribute("firstOfMonth", firstOfMonth);
        model.addAttribute("lastOfMonth", lastOfMonth);
        model.addAttribute("calendarWeeks", calendarWeeks);
        model.addAttribute("monthlyAppointments", monthlyAppointments);

        // Navigation for previous/next month
        model.addAttribute("prevMonth", yearMonth.minusMonths(1));
        model.addAttribute("nextMonth", yearMonth.plusMonths(1));

        return "patient/calendar/view";
    }
}