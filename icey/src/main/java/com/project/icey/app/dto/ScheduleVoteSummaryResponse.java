package com.project.icey.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;

@Getter
@AllArgsConstructor
public class ScheduleVoteSummaryResponse {
    private int maxCount;

    private List<SummaryByDate> summary;

    @Getter
    @AllArgsConstructor
    public static class SummaryByDate {
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM.dd")
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
