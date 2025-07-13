package com.project.icey.app.domain;

import jakarta.persistence.*;

@Entity
public class ScheduleVote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_TEAM_MANAGER_ID")
    private UserTeamManager userTeamManager;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CANDIDATE_DATE_ID")
    private CandidateDate candidateDate;
}
