package com.project.icey.app.dto;

import com.project.icey.app.domain.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    private NotificationType type;
    private String teamName;
}
