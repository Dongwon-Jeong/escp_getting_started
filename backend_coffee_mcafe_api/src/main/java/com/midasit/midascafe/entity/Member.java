package com.midasit.midascafe.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member extends BaseEntity {

    @Id
    @Column(name="member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("사용자 id")
    private Long id;

    @Column(nullable = false)
    @Comment("구성원의 휴대폰 번호")
    private String phone;

    @Column(nullable = false)
    @Comment("구성원의 이름")
    private String name;


    @OneToMany(mappedBy = "member")
    private List<TeamMember> teamMembers = new ArrayList<>();

}
