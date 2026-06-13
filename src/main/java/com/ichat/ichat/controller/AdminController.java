
package com.ichat.ichat.controller;

import com.ichat.ichat.model.Doctor;
import com.ichat.ichat.model.Role;
import com.ichat.ichat.model.User;
import com.ichat.ichat.repository.DoctorRepository;
import com.ichat.ichat.repository.RoleRepository;
import com.ichat.ichat.repository.UserRepository;
import com.ichat.ichat.service.AppointmentService;
import com.ichat.ichat.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private RoleRepository roleRepository;



    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !user.getRoles().contains("ROLE_ADMIN")) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        return "admin/dashboard";
    }

    @GetMapping("/users")
    @ResponseBody
    public List<User> getAllUsers(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !user.getRoles().contains("ROLE_ADMIN")) {
            return List.of();
        }
        return userService.getAllUsers();
    }
    @PostMapping("/users/delete")
    @ResponseBody
    public String deleteUsers(@RequestParam List<Long> ids, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !user.getRoles().contains("ROLE_ADMIN")) {
            return "unauthorized";
        }
        userService.deleteById(ids);
        return "success";
    }


    @GetMapping("/add-user")
    public String addUserForm(Model model) {
        model.addAttribute("user", new User());
        return "admin/add_user";
    }

    @PostMapping("/add-user")
    public String addUser(@RequestParam String email,
                          @RequestParam String firstName,
                          @RequestParam String lastName,
                          @RequestParam String password,
                          @RequestParam String role,
                          @RequestParam(name = "designation", required = false )String designation,
                          @RequestParam(name = "specialization", required = false )String specialization) {

        User user = new User();
        user.setUsername(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPassword(password);
       // Role userRole = roleRepository.findByRoleName(role)
               // .orElseThrow(() -> new RuntimeException("Default role not found"));
        user.setRoles(Set.of(role));

        User addedUser = userRepository.save(user);
        if(role.equals("ROLE_DOCTOR")) {
            Doctor doctor = new Doctor();
            doctor.setUser(addedUser);
            doctor.setDesignation(designation);
            doctor.setSpecialization(specialization);
            Doctor savedDoctor = doctorRepository.save(doctor);

        }

        return "redirect:/admin/dashboard";
    }
    /*@PostMapping("/create-user")
    public String createUser(@RequestParam String email,
                             @RequestParam String firstName,
                             @RequestParam String lastName,
                             @RequestParam String password,
                             @RequestParam String role) {

        User user = new User();
        user.setUsername(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPassword(password);
        user.setRoles(Set.of(role));

        userRepository.save(user);
        return "redirect:/admin/dashboard";
    }*/

    @PostMapping("/deactivate/{id}")
    public String deactivateUser(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user != null) {
            user.setEnabled(false);
            userService.saveUser(user);
        }
        return "redirect:/admin/users";
    }


}
