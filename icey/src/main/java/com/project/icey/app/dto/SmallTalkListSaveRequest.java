package com.project.icey.app.dto;

import lombok.Data;

import java.util.List;

@Data
public class SmallTalkListSaveRequest {
    private String target;
    private String purpose;
    private String title;
    private Integer change_time;
    private List<SmallTalkDto> smallTalks;
}
