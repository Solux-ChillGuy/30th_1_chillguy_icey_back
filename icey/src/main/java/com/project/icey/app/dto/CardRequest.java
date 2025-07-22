package com.project.icey.app.dto;

//사용자가 카드 생성 시 입력하는 정보를 담는 DTO!
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CardRequest {
    @NotBlank
    private String adjective;
    @NotBlank
    private String animal;
    @NotBlank
    private String mbti;
    @NotBlank
    private String hobby;
    @NotBlank
    private String secretTip;
    @NotBlank
    private String tmi;
    @NotBlank
    private String accessory;
    //프로필 색은 null 가능
    private String profileColor;


}
