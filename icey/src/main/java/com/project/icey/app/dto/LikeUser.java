package com.project.icey.app.dto;

import com.project.icey.app.domain.AccessoryType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LikeUser {
    private String profileColor;
    private String animal;
    private AccessoryType accessory;
}

