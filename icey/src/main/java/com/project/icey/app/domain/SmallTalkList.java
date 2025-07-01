package com.project.icey.app.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SmallTalkList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ userId 대신 User 객체로 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    private String target;   // 스몰톡 대상
    private String purpose;  // 스몰톡 목적
    private String title;    // 리스트 제목

    @OneToMany(mappedBy = "smallTalkList", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SmallTalk> smallTalks = new ArrayList<>();
}