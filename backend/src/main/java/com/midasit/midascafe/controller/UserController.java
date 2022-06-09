package com.midasit.midascafe.controller;

import com.midasit.midascafe.entity.User;
import com.midasit.midascafe.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
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

    @GetMapping("/users/test-set1")
    public void SetUsers1() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("USER");

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        tx.begin();

        User tempUser = new User();
//        tempUser.setId(1);
        tempUser.setName("bcd");

        em.persist(tempUser);
        tx.commit();

        em.close();
        emf.close();
    }
}
