package com.project.icey.app.service;

import com.project.icey.app.domain.*;
import com.project.icey.app.dto.ScheduleCreateRequest;
import com.project.icey.app.dto.ScheduleVoteDTO;
import com.project.icey.app.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ScheduleService {
    private final TeamRepository teamRepository;
    private final UserTeamRepository utmRepository;
    private final ScheduleRepository scheduleRepository;
    private final CandidateDateRepository candidateDateRepository;
    private final NotificationService notificationService;
    private final ScheduleTimeSlotRepository scheduleTimeSlotRepository;
    private final ScheduleVoteRepository scheduleVoteRepository;

    //약속잡기 투표 생성
    public void createSchedule(Long teamId, Long userId, ScheduleCreateRequest request) {

        //팀 조회
        Team team = teamRepository.findByIdWithMembersAndUsers(teamId)
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

        for (LocalDate date : request.getCandidateDates()) {
            CandidateDate candidateDate = CandidateDate.builder()
                    .date(date)
                    .schedule(schedule)
                    .build();

            candidateDateRepository.save(candidateDate);

            // 9시 ~ 24시 시간 슬롯 생성
            for (int hour = 9; hour <= 24; hour++) {
                ScheduleTimeSlot timeSlot = ScheduleTimeSlot.builder()
                        .hour(hour)
                        .candidateDate(candidateDate)
                        .build();

                scheduleTimeSlotRepository.save(timeSlot);
            }
        }

        String message = team.getTeamName() + "로부터 약속잡기가 생성되었습니다.";

        List<UserTeamManager> members = team.getMembers();
        for (UserTeamManager member : members) {
            Long memberUserId = member.getUser().getId();
            if (!memberUserId.equals(userId)) {
                notificationService.sendNotification(
                        member.getUser().getId(),
                        NotificationType.APPOINTMENT_CREATED,
                        message
                );
            }
        }

    }

    //약속 잡기 투표
    @Transactional
    public void submitVote(Long teamId, Long userId, ScheduleVoteDTO request){
        // 팀 구성원인지 확인
        UserTeamManager voter = utmRepository.findByUserIdAndTeam_TeamId(userId, teamId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저는 팀 멤버가 아닙니다."));

        // 스케줄 투표가 존재하는지 확인
        Schedule schedule = scheduleRepository.findByTeam_TeamId(teamId)
                .orElseThrow(() -> new IllegalArgumentException("해당 팀에 스케줄이 존재하지 않습니다."));

        //기존에 있던 투표가 있었다면 싹 밀고 그냥 다시 쌓기
        scheduleVoteRepository.deleteByVoterId(voter.getId());

        List<ScheduleVote> newVotes = new ArrayList<>();

        //먼저 날짜별로 이제 해당 타임 슬로
        for (ScheduleVoteDTO.VoteByDate voteByDate : request.getVotes()) {
            CandidateDate candidateDate = candidateDateRepository
                    .findBySchedule_ScheduleIdAndDate(schedule.getScheduleId(), voteByDate.getDate())
                    .orElseThrow(() -> new IllegalArgumentException("해당 날짜 후보가 존재하지 않습니다."));

            for (Integer hour : voteByDate.getHours()) {
                ScheduleTimeSlot slot = scheduleTimeSlotRepository
                        .findByCandidateDateIdAndHour(candidateDate.getId(), hour)
                        .orElseThrow(() -> new IllegalArgumentException("해당 시간대가 존재하지 않습니다."));

                ScheduleVote vote = ScheduleVote.builder()
                        .voter(voter)
                        .timeSlot(slot)
                        .build();

                newVotes.add(vote);
            }
        }
        scheduleVoteRepository.saveAll(newVotes);

    }
    @Transactional(readOnly = true)
    public ScheduleVoteDTO getMyVotes(Long teamId, Long userId) {
        // 팀 구성원인지 확인
        UserTeamManager voter = utmRepository.findByUserIdAndTeam_TeamId(userId, teamId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저는 팀 멤버가 아닙니다."));

        // 스케줄 조회
        Schedule schedule = scheduleRepository.findByTeam_TeamId(teamId)
                .orElseThrow(() -> new IllegalArgumentException("해당 팀에 스케줄이 존재하지 않습니다."));

        // 유저가 투표한 ScheduleVote 전부 조회
        List<ScheduleVote> myVotes = scheduleVoteRepository.findByVoterId(voter.getId());

        // 날짜별 그룹핑
        Map<LocalDate, List<Integer>> grouped = myVotes.stream()
                .collect(Collectors.groupingBy(
                        vote -> vote.getTimeSlot().getCandidateDate().getDate(),
                        Collectors.mapping(vote -> vote.getTimeSlot().getHour(), Collectors.toList())
                ));

        List<ScheduleVoteDTO.VoteByDate> result = grouped.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new ScheduleVoteDTO.VoteByDate(entry.getKey(), entry.getValue()))
                .toList();

        return new ScheduleVoteDTO(result);
    }


}
