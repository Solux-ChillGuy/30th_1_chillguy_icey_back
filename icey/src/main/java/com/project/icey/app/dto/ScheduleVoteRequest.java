package com.project.icey.app.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

//특정사용자의 투표

@Getter
@Setter
public class ScheduleVoteRequest {

    private List<VoteByDate> votes;

    @Getter
    @Setter
    public static class VoteByDate {
        private LocalDate date;
        private List<Integer> hours;
    }
}
