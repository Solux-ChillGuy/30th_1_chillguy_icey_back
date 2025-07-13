package com.project.icey.app.service;

import com.project.icey.app.domain.*;
import com.project.icey.app.dto.ScheduleCreateRequest;
import com.project.icey.app.repository.CandidateDateRepository;
import com.project.icey.app.repository.ScheduleRepository;
import com.project.icey.app.repository.TeamRepository;
import com.project.icey.app.repository.UserTeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final TeamRepository teamRepository;
    private final UserTeamRepository utmRepository;
    private final ScheduleRepository scheduleRepository;
    private final CandidateDateRepository candidateDateRepository;

    public void createSchedule(Long teamId, Long userId, ScheduleCreateRequest request){

        //팀 조회
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("해당 팀이 존재하지 않습니다."));

        //팀장인지 조회해서 권한 확인하고
        UserTeamManager creator = utmRepository.findByUserIdAndTeam_TeamId(userId, teamId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저는 이 팀의 구성원이 아닙니다."));

        //이미 스케줄이 존재하는지 확인해서 => 이미 있으면 못만드니까
        if (creator.getRole() != UserRole.LEADER) {
            throw new IllegalStateException("팀장만 스케줄을 생성할 수 있습니다.");
        }

        //Schedule 생성
        if (scheduleRepository.existsByTeam(team)) {
            throw new IllegalStateException("이미 이 팀에는 스케줄 투표가 존재합니다.");
        }

        Schedule schedule = Schedule.builder()
                .team(team)
                .creator(creator)
                .build();

        scheduleRepository.save(schedule);

        List<CandidateDate> candidateDates = request.getCandidateDates().stream()
                .map(date -> CandidateDate.builder()
                        .date(date)
                        .schedule(schedule)
                        .build())
                .toList();

        candidateDateRepository.saveAll(candidateDates);
    }

}
