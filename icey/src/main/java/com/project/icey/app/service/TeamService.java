package com.project.icey.app.service;

import com.project.icey.app.domain.Team;
import com.project.icey.app.repository.TeamRepository;
import com.project.icey.app.repository.UserRepository;
import com.project.icey.app.repository.UserTeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeamService {
    private final TeamRepository teamRepository;
    private final UserTeamRepository userteamRepository;

    /*public Team createTeam(String teamName,  memberNum)*/
}
