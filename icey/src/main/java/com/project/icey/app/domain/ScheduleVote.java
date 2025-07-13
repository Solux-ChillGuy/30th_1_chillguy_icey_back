package com.project.icey.app.domain;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class ScheduleVote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_TEAM_MANAGER_ID")
    private UserTeamManager voter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SCHEDULE_ID")
    private Schedule schedule;

    private LocalDate date;
    private int hour;
}
