package com.midasit.midascafe.entity;

import lombok.Getter;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {
    @CreatedDate
    @CreationTimestamp
    @Comment("생성 일자")
    @Column(nullable = false)
    private LocalDateTime createDateTime;

    @LastModifiedDate
    @UpdateTimestamp
    @Comment("수정 일자")
    @Column(nullable = false)
    private LocalDateTime updateDateTime;
}
