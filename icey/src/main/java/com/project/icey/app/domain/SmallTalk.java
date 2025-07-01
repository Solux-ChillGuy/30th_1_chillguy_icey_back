package com.project.icey.app.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SmallTalk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String question;
    private String tip;
    private String answer; // 사용자가 작성하는 답변

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "small_talk_list_id")
    private SmallTalkList smallTalkList;
}