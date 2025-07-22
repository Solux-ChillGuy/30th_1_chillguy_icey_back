package com.project.icey.app.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class ScheduleVoteResponse {

    private List<VoteByDate> myVotes;

    @Getter
    @AllArgsConstructor
    public static class VoteByDate {
        @JsonFormat(pattern = "YYYY-MM-dd")
        private LocalDate date;
        private List<Integer> hours;
    }
}
