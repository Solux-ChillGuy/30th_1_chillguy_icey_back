package com.project.icey.app.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "MEMO")
@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED) @AllArgsConstructor @Builder
public class Memo {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMO_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "UTM_ID")
    private UserTeamManager utm;

    @Lob @Column(nullable = false, length = 300)
    private String content;

    @CreationTimestamp
    private LocalDateTime createdAt = LocalDateTime.now();

    public void updateContent(String c) { this.content = c; }
}
