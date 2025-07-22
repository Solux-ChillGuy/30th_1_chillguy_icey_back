package com.project.icey.app.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class MajorityTimeResponse {

    private List<AvailableTimeByDate> results;

    @Getter
    @AllArgsConstructor
    public static class AvailableTimeByDate {
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "YYYY-MM-dd")
        private LocalDate date;
        private List<String> availableHours;
    }
}

