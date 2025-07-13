package com.project.icey.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class ScheduleVoteSummaryResponse {
    private List<SummaryByDate> summary;

    @Getter
    @AllArgsConstructor
    public static class SummaryByDate {
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
