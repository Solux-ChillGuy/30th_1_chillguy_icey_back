package com.project.icey.app.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "CARD")
public class Card {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CARD_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM_ID")
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORIGIN_CARD_ID")
    private Card origin; // 원본 템플릿 참조

    @Column(name = "ADJECTIVE", length = 30)
    private String adjective;

    @Column(name = "ANIMAL", length = 30)
    private String animal;

    @Column(name = "MBTI", length = 4)
    private String mbti;

    @Column(name = "HOBBY", length = 100)
    private String hobby;

    @Column(name = "SECRET_TIP", length = 100)
    private String secretTip;

    @Column(name = "TMI", length = 100)
    private String tmi;

    @Column(name = "PROFILE_COLOR", length = 20)
    private String profileColor; // 템플릿은 null, 팀카드만 색상

    @Enumerated(EnumType.STRING)
    @Column(name = "ACCESSORY", length = 30)
    private AccessoryType accessory;

    @Column(name = "NICKNAME", length = 60)
    private String nickname;

    @CreationTimestamp
    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    /* ==== 닉네임 규칙 ==== */
    public void regenerateNickname() {
        this.nickname = String.join(" ", adjective, animal);
    }

    @Builder
    private Card(User user, Team team, Card origin,
                 String adjective, String animal, String mbti,
                 String hobby, String secretTip, String tmi,
                 String profileColor, AccessoryType accessory) {
        this.user = user;
        this.team = team;
        this.origin = origin;
        this.adjective = adjective;
        this.animal = animal;
        this.mbti = mbti;
        this.hobby = hobby;
        this.secretTip = secretTip;
        this.tmi = tmi;
        this.profileColor = profileColor;
        this.accessory = accessory;
        regenerateNickname();
    }
}