package com.project.icey.app.domain;

import jakarta.persistence.*;
import lombok.*;
import net.minidev.json.annotate.JsonIgnore;
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

    //userteamrepository에서 멤버 수 조회 메서드를 활용할 예정.
    //private Integer memberNum; -> 제거하기로 결정.

    @Column(updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime expiration;

    private String invitation;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
        @Builder.Default    //빌드 오류나서 우선 추가해보았습니다   -서현,,,,
    private List<UserTeamManager> members = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.expiration = LocalDateTime.now().plus(Duration.ofDays(30));

    }

}
