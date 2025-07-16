package com.project.icey.app.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.AttributeBinderType;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scheduleId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM_ID")
    private Team team;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CREATOR_ID")
    private UserTeamManager creator;

    @CreationTimestamp
    private LocalDateTime createdAt;

    //여러 후보들 받을거니까
    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CandidateDate> candidateDates = new ArrayList<>();

    // 확정된 날짜
    private LocalDate confirmedDate;

    // 확정된 시간 (예: 시간대만 정수로 저장)
    private Integer confirmedHour;

    // 확정 여부
    private boolean confirmed;

}
