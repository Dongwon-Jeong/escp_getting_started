package com.midasit.midascafe.domain.team;

import com.midasit.midascafe.base.BaseEntity;
import com.midasit.midascafe.domain.member.Member;
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
public class TeamMember extends BaseEntity {
    @Id
    @Column(name="team_member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("팀 멤버 조인 pk")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;
}
