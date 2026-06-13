package com.ichat.ichat.service;

import com.ichat.ichat.model.Role;
import com.ichat.ichat.model.User;
import com.ichat.ichat.repository.RoleRepository;
import com.ichat.ichat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public void saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Role role = roleRepository.findByRoleName("ROLE_PATIENT").orElseThrow();
        user.setRoles(new HashSet<>());
        user.getRoles().add(role.getRoleName());

        userRepository.save(user);
    }
    @Override
    public User updateUser(User user) {


        return userRepository.save(user);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
    @Override
    public User registerUser(User user) {

        Role patientRole = roleRepository.findByRoleName("ROLE_PATIENT")
                .orElseThrow(() -> new RuntimeException("Default role not found"));
        user.setRoles(Set.of(patientRole.getRoleName()));

        User updateduser =  userRepository.save(user);
        return updateduser;
    }
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User findById(Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.orElse(null);
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public void deleteById(List<Long> ids) {
        ids.forEach(userRepository::deleteById);
    }

}
