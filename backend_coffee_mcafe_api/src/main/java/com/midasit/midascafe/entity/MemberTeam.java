package com.midasit.midascafe.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MemberTeam extends BaseEntity{
    @Id
    @Column(name="member_team_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("사용자 id")
    private Long id;
}
