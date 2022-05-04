package com.midasit.midascafe.controller;

import com.midasit.midascafe.entity.User;
import com.midasit.midascafe.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class UserController {
    private final  UserRepository userRepository;

    @GetMapping("/users/test-set")
    public void SetUsers() {
        User testUser = User.register("kim");
        userRepository.save(testUser);
    }

    @GetMapping("/users/test-get")
    public Integer GetUsers() {
        List<User> users = userRepository.findAll();
        return users.size();
    }
}
