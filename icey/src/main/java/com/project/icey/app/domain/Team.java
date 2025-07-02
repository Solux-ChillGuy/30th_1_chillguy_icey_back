package com.project.icey.app.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import javax.naming.Context;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long teamId;

    private String teamName;
    private int memberNum;

    private LocalDateTime createdAt;
    private LocalDateTime Expiration;

    private String invitation;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.Expiration = LocalDateTime.now().plus(Duration.ofDays(30));

    }

}
