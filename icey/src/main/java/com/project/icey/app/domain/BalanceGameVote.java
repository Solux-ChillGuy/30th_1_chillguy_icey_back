package com.project.icey.app.domain;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BalanceGameVote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "balance_game_id")
    private BalanceGame balanceGame;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 1 or 2 중 선택
    private int selectedOption;

    private LocalDateTime votedAt;

    @PrePersist
    public void prePersist() {
        if (votedAt == null) votedAt = LocalDateTime.now();
    }
}