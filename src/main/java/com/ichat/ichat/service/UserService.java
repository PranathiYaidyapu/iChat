package com.ichat.ichat.service;

import com.ichat.ichat.model.User;

import java.util.List;

public interface UserService {
    void saveUser(User user);
    User findByUsername(String username);

    User registerUser(User user);
    List<User> getAllUsers();
    User findById(Long id);
    void deleteById(Long id);
    void deleteById(List<Long> ids);
    User updateUser(User user);

}
