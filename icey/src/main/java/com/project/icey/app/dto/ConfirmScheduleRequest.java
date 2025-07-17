package com.project.icey.app.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmScheduleRequest {
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    private int hour;
}
