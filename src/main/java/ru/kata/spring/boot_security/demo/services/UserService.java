package ru.kata.spring.boot_security.demo.services;

import org.springframework.security.core.userdetails.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> findAll(); //
    Optional<User> findByUsername(String username); //
    User findUserById(Long id); //
    void updateUser(User user, Long id); //
    void saveUser (User user); //
    boolean deleteUserById(Long id);//

}
