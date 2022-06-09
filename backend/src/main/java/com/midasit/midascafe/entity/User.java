package com.midasit.midascafe.entity;

import javax.persistence.*;

@Entity
public class User {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;
    @Column(
            length = 32,
            nullable = false
    )
    private String name;

    ////////////////////////////////////////////////////////

    public static User register(String name) {
        User user = new User();
        user.name = name;
        return user;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
