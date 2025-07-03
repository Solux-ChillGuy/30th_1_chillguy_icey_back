package com.project.icey.app.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import javax.naming.Context;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "TEAM")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long teamId;

    private String teamName;
    private Integer memberNum;

    @Column(updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime expiration;

    private String invitation;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    private List<UserTeamManager> members = new ArrayList<>();


    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.expiration = LocalDateTime.now().plus(Duration.ofDays(30));

    }

}
