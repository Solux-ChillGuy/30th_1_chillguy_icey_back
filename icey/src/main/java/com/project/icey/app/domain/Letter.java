package com.project.icey.app.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Letter {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long letterId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM_ID", nullable = false)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_card_id", nullable = false)
    private Card senderCard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_card_id", nullable = false)
    private Card receiverCard;

    private String content;

    @Column(nullable = false)
    private LocalDateTime sentAt;

    @Column(nullable = false)
    private boolean isRead;

}
