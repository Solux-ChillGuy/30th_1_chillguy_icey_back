package com.project.icey.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BalanceGameResultDto {
    private String option1;
    private long option1Count;
    private String option2;
    private long option2Count;
    private long totalVotes;
}