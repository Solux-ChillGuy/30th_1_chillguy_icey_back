package com.project.icey.app.dto;

//사용자가 카드 생성 시 입력하는 정보를 담는 DTO!
import com.project.icey.app.domain.AccessoryType;
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
    private AccessoryType accessory;
    //프로필 색은 null 가능
    // 리퀘스트에서 제거하고 백에서만 관리해보기>private String profileColor;


}
