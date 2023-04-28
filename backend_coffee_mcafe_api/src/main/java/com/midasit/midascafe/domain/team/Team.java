package com.midasit.midascafe.domain.team;

import com.midasit.midascafe.base.BaseEntity;
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
public class Team extends BaseEntity {
    @Id
    @Column(name = "team_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("Team pk")
    private Long id;

    @Column(nullable = false, length = 40)
    @Comment("이름")
    private String name;


    @OneToMany(mappedBy = "team")
    private List<TeamMember> teamMembers = new ArrayList<>();
}
