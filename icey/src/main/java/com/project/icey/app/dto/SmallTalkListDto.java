package com.project.icey.app.dto;

import com.project.icey.app.domain.SmallTalkList;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class SmallTalkListDto {
    private Long id;
    private String target;
    private String purpose;
    private String title;
    private List<SmallTalkDto> smallTalks;
    private LocalDateTime createdAt;
    private Integer change_time;


    public SmallTalkListDto() {
    }

    public SmallTalkListDto(Long id, String target, String purpose, String title, Integer change_time, List<SmallTalkDto> smallTalks) {
        this.id = id;
        this.target = target;
        this.purpose = purpose;
        this.title = title;
        this.change_time = change_time;
        this.smallTalks = smallTalks;
    }


    public SmallTalkListDto(Long id, String target, String purpose, String title, List<SmallTalkDto> smallTalks, LocalDateTime createdAt) {
        this.id = id;
        this.target = target;
        this.purpose = purpose;
        this.title = title;
        this.smallTalks = smallTalks;
        this.createdAt = createdAt;
    }

    public static SmallTalkListDto fromEntity(SmallTalkList entity) {
        SmallTalkListDto dto = new SmallTalkListDto();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setChange_time(entity.getChange_time());
        return dto;
    }

}
