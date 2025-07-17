package com.project.icey.app.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CandidateDate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date; //년/월/일 받을거니까

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SCHEDULE_ID")
    private Schedule schedule;

    //이제 실제 투표와 연결
    @OneToMany(mappedBy = "candidateDate", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ScheduleTimeSlot> timeSlots = new ArrayList<>();
}
