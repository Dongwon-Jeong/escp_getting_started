package com.midasit.midascafe.domain.pay;

import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@RequiredArgsConstructor
public class PayLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @CreatedDate
    LocalDateTime createdDateTime;

    @NotNull
    @Column(length = 5000)
    String url;

    public PayLog(String url) {
        this.url = url;
    }
}
