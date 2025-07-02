package com.project.icey.app.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class UserTeamManager {

    @Id
    @GeneratedValue
    @Column(name = "TEAMMANAGER_ID")
    private long id;

    @ManyToOne
    private Team team;

    @ManyToOne
    private User user;

    //nickname은 추후 card와 연결
    private String nickname;

    private String role;


}
