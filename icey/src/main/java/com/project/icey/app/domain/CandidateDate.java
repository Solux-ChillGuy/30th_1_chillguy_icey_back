package com.project.icey.app.domain;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CandidateDate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date; //년/월/일 받을거니까

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SCHEDULE_ID")
    private Schedule schedule;

    //이제 실제 투표와 연결
    @OneToMany(mappedBy = "candidateTimeSlot", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ScheduleVote> votes = new ArrayList<>();
}
