package com.project.icey.app.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleTimeSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int hour; // 9~24시까지의 timeslot

    private int voteCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CANDIDATE_DATE_ID", nullable = false)
    private CandidateDate candidateDate;
}
