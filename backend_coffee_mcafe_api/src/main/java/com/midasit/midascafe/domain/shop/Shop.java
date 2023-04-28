package com.midasit.midascafe.domain.shop;

import com.midasit.midascafe.base.BaseEntity;
import org.hibernate.annotations.Comment;

import javax.persistence.*;

@Entity
public class Shop extends BaseEntity {
    @Id
    @Column(name = "shop_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("주문 id")
    private Long id;


    // Team

    // Member
}
