package com.midasit.midascafe.entity;

import org.hibernate.annotations.Comment;

import javax.persistence.*;

@Entity
public class Orders extends BaseEntity {
    @Id
    @Column(name = "order_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("주문 id")
    private Long id;

}
