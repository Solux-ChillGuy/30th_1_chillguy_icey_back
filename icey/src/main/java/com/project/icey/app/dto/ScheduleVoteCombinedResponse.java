package com.project.icey.app.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ScheduleVoteCombinedResponse {
    private List<VoteByDateResponse> myVotes;
    private List<SummaryByDateResponse> summary;
    private int maxVoteCount;

    @Getter
    @AllArgsConstructor
    public static class VoteByDateResponse {
        @JsonFormat(pattern = "MM.dd")
        private LocalDate date;
        private List<Integer> hours;
    }

    @Getter
    @AllArgsConstructor
    public static class SummaryByDateResponse {
        @JsonFormat(pattern = "MM.dd")
        private LocalDate date;
        private List<HourVote> hourVotes;
    }

    @Getter
    @AllArgsConstructor
    public static class HourVote {
        private int hour;
        private int count;
    }
}
