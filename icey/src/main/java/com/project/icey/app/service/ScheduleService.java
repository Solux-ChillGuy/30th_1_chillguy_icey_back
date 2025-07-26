package com.project.icey.app.service;

import com.project.icey.app.domain.*;
import com.project.icey.app.dto.*;
import com.project.icey.app.repository.*;
import com.project.icey.global.exception.ErrorCode;
import com.project.icey.global.exception.model.CoreApiException;
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
    public ScheduleVoteCombinedResponse createSchedule(Long teamId, Long userId, ScheduleCreateRequest request) {

        //팀 조회
        Team team = teamRepository.findByIdWithMembersAndUsers(teamId)
                .orElseThrow(() -> new CoreApiException(ErrorCode.TEAM_NOT_FOUND));

        //팀장인지 조회해서 권한 확인하고
        UserTeamManager creator = utmRepository.findByUserIdAndTeam_TeamId(userId, teamId)
                .orElseThrow(() -> new CoreApiException(ErrorCode.USER_NOT_IN_TEAM));

        //이미 스케줄이 존재하는지 확인해서 => 이미 있으면 못만드니까
        if (creator.getRole() != UserRole.LEADER) {
            throw new CoreApiException(ErrorCode.NOT_LEADER);
        }
        if (scheduleRepository.existsByTeam(team)) {
            throw new CoreApiException(ErrorCode.SCHEDULE_ALREADY_EXISTS);        }

        //Schedule 생성
        Schedule schedule = Schedule.builder()
                .team(team)
                .creator(creator)
                .build();

        scheduleRepository.save(schedule);

        for (LocalDate date : request.getCandidateDates()) {
            CandidateDate candidateDate = CandidateDate.builder()
                    .date(date)
                    .schedule(schedule)
                    .timeSlots(new ArrayList<>())
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
        //String message = String.format("[%s]로부터 약속잡기가 생성되었습니다.", schedule.getTeam().getTeamName());

        List<UserTeamManager> members = team.getMembers();
        for (UserTeamManager member : members) {
            Long memberUserId = member.getUser().getId();
            if (!memberUserId.equals(userId)) {
                notificationService.sendNotification(
                        member.getUser().getId(),
                        NotificationType.APPOINTMENT_CREATED,
                        team.getTeamName()
                );
            }
        }
        //빈 맨 처음 초기화된 투표 생성
        return getCombinedVoteResult(teamId, userId);
    }

    //약속 잡기 투표
    @Transactional
    public ScheduleVoteCombinedResponse submitVote(Long teamId, Long userId, ScheduleVoteRequest request){
        // 팀 구성원인지 확인
        UserTeamManager voter = utmRepository.findByUserIdAndTeam_TeamId(userId, teamId)
                .orElseThrow(() -> new CoreApiException(ErrorCode.USER_NOT_IN_TEAM));

        // 스케줄 투표가 존재하는지 확인
        Schedule schedule = scheduleRepository.findByTeam_TeamId(teamId)
                .orElseThrow(() -> new CoreApiException(ErrorCode.SCHEDULE_NOT_FOUND));

        //기존에 있던 투표가 있었다면 싹 밀고 그냥 다시 쌓기
        scheduleVoteRepository.deleteByVoterId(voter.getId());

        List<ScheduleVote> newVotes = new ArrayList<>();

        //먼저 날짜별로 이제 해당 타임 슬로
        for (ScheduleVoteRequest.VoteByDate voteByDate : request.getVotes()) {
            CandidateDate candidateDate = candidateDateRepository
                    .findBySchedule_ScheduleIdAndDate(schedule.getScheduleId(), voteByDate.getDate())
                    .orElseThrow(() ->  new CoreApiException(ErrorCode.CANDIDATE_DATE_NOT_FOUND));

            for (Integer hour : voteByDate.getHours()) {
                ScheduleTimeSlot slot = scheduleTimeSlotRepository
                        .findByCandidateDateIdAndHour(candidateDate.getId(), hour)
                        .orElseThrow(() -> new CoreApiException(ErrorCode.TIME_SLOT_NOT_FOUND));

                ScheduleVote vote = ScheduleVote.builder()
                        .voter(voter)
                        .timeSlot(slot)
                        .build();

                newVotes.add(vote);
            }
        }
        scheduleVoteRepository.saveAll(newVotes);

        // 투표 인원수 확인해서 팀장에게 알림
        Long votedCount = scheduleVoteRepository.countDistinctVotersByScheduleId(schedule.getScheduleId());
        int totalMembers = schedule.getTeam().getMembers().size();

        if(votedCount != null && votedCount == totalMembers) {
            UserTeamManager leader = schedule.getTeam().getMembers().stream()
                    .filter(member -> member.getRole() == UserRole.LEADER)
                    .findFirst()
                    .orElse(null);

            //String message = String.format("[%s]의 팀원 모두가 가능한 시간대를 등록했습니다. 약속을 확정해보세요!", schedule.getTeam().getTeamName());


            if (leader != null) {
                notificationService.sendNotification(
                        leader.getUser().getId(),
                        NotificationType.APPOINTMENT_COMPLETED,
                        schedule.getTeam().getTeamName()
                );
            }
        }


        return getCombinedVoteResult(teamId, userId);
    }

    //내가 투표한거 조회
    @Transactional(readOnly = true)
    public ScheduleVoteResponse getMyVotes(Long teamId, Long userId) {
        // 팀 구성원인지 확인
        UserTeamManager voter = utmRepository.findByUserIdAndTeam_TeamId(userId, teamId)
                .orElseThrow(() -> new CoreApiException(ErrorCode.USER_NOT_IN_TEAM));

        // 스케줄 조회하고
        Schedule schedule = scheduleRepository.findByTeam_TeamId(teamId)
                .orElseThrow(() -> new CoreApiException(ErrorCode.SCHEDULE_NOT_FOUND));

        // 유저가 투표한 ScheduleVote 전부 가져와서
        List<ScheduleVote> myVotes = scheduleVoteRepository.findByVoterId(voter.getId());

        // 날짜별 묶어가지고 반환
        Map<LocalDate, List<Integer>> grouped = myVotes.stream()
                .collect(Collectors.groupingBy(
                        vote -> vote.getTimeSlot().getCandidateDate().getDate(),
                        Collectors.mapping(vote -> vote.getTimeSlot().getHour(), Collectors.toList())
                ));

        List<ScheduleVoteResponse.VoteByDate> result = grouped.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new ScheduleVoteResponse.VoteByDate(entry.getKey(), entry.getValue()))
                .toList();

        return new ScheduleVoteResponse(result);
    }

    //팀의 투표현황 요약 조회
    @Transactional(readOnly = true)
    public ScheduleVoteSummaryResponse getVoteSummary(Long teamId) {
        Schedule schedule = scheduleRepository.findByTeam_TeamId(teamId)
                .orElseThrow(() -> new CoreApiException(ErrorCode.SCHEDULE_NOT_FOUND));

        // 후보 날짜들 일단 갖고와서
        List<CandidateDate> candidateDates = candidateDateRepository.findBySchedule_ScheduleId(schedule.getScheduleId());
        List<ScheduleVoteSummaryResponse.SummaryByDate> result = new ArrayList<>();

        int maxCount = 0;
        // 후보 날짜들에 따라 투표 내용들을 모아서 반환 => 인원수 갖고오기 slot의 votes 활용
        for (CandidateDate candidateDate : candidateDates) {
            List<ScheduleVoteSummaryResponse.HourVote> hourVotes = new ArrayList<>();

            for (ScheduleTimeSlot slot : candidateDate.getTimeSlots()) {
                int count = slot.getVotes().size(); // 이 시간대에 투표한 인원 수
                hourVotes.add(new ScheduleVoteSummaryResponse.HourVote(slot.getHour(), count));
                maxCount = Math.max(maxCount, count);
            }

            result.add(new ScheduleVoteSummaryResponse.SummaryByDate(candidateDate.getDate(), hourVotes));
        }

        return new ScheduleVoteSummaryResponse(maxCount, result);
    }
    
    //팀 현황 + 내 투표 정보 통합조회

    @Transactional(readOnly = true)
    public ScheduleVoteCombinedResponse getCombinedVoteResult(Long teamId, Long userId) {
        // 팀/스케줄/유저 확인
        UserTeamManager voter = utmRepository.findByUserIdAndTeam_TeamId(userId, teamId)
                .orElseThrow(() -> new CoreApiException(ErrorCode.USER_NOT_IN_TEAM));
        Schedule schedule = scheduleRepository.findByTeam_TeamId(teamId)
                .orElseThrow(() -> new CoreApiException(ErrorCode.SCHEDULE_NOT_FOUND));

        // 내 투표 정보 가져오기
        List<ScheduleVote> myVotes = scheduleVoteRepository.findByVoterId(voter.getId());
        Map<LocalDate, List<Integer>> myVotesGrouped = myVotes.stream()
                .collect(Collectors.groupingBy(
                        v -> v.getTimeSlot().getCandidateDate().getDate(),
                        Collectors.mapping(v -> v.getTimeSlot().getHour(), Collectors.toList())
                ));
        List<ScheduleVoteCombinedResponse.VoteByDateResponse> myVotesResult = myVotesGrouped.entrySet().stream()
                .map(entry -> new ScheduleVoteCombinedResponse.VoteByDateResponse(entry.getKey(), entry.getValue()))
                .toList();

        // 전체 요약 정보 만들기
        List<CandidateDate> candidateDates = candidateDateRepository.findWithTimeSlotsByScheduleId(schedule.getScheduleId());
        List<ScheduleVoteCombinedResponse.SummaryByDateResponse> summary = new ArrayList<>();
        int maxCount = 0;

        for (CandidateDate date : candidateDates) {
            List<ScheduleVoteCombinedResponse.HourVote> hourVotes = new ArrayList<>();

            for (ScheduleTimeSlot slot : date.getTimeSlots()) {
                int count = slot.getVotes().size();
                maxCount = Math.max(maxCount, count);
                hourVotes.add(new ScheduleVoteCombinedResponse.HourVote(slot.getHour(), count));
            }

            summary.add(new ScheduleVoteCombinedResponse.SummaryByDateResponse(date.getDate(), hourVotes));
        }

        return new ScheduleVoteCombinedResponse(myVotesResult, summary, maxCount);
    }

    //과반수 이상 가능한 시간대 조회
    public MajorityTimeResponse getMajorityAvailableTimeForAllDates(Long teamId) {

        Team team = teamRepository.findByIdWithMembersAndUsers(teamId)
                .orElseThrow(() -> new CoreApiException(ErrorCode.TEAM_NOT_FOUND));
        int totalMembers = team.getMembers().size();
        int majority = (totalMembers / 2) + 1;

        Schedule schedule = scheduleRepository.findByTeam_TeamId(teamId)
                .orElseThrow(() -> new CoreApiException(ErrorCode.SCHEDULE_NOT_FOUND));

        List<CandidateDate> candidateDates = candidateDateRepository
                .findBySchedule_ScheduleId(schedule.getScheduleId());

        List<MajorityTimeResponse.AvailableTimeByDate> results = candidateDates.stream()
                .map(candidateDate -> {
                    List<String> availableHours = candidateDate.getTimeSlots().stream()
                            .filter(slot -> slot.getVotes().size() >= majority)
                            .map(slot -> convertToTimeFormat(slot.getHour()))
                            .collect(Collectors.toList());

                    return new MajorityTimeResponse.AvailableTimeByDate(candidateDate.getDate(), availableHours);
                })
                .collect(Collectors.toList());

        return new MajorityTimeResponse(results);
    }

    private String convertToTimeFormat(int hour) {
        int normalized = hour % 24;
        if (normalized == 0) {
            return "AM 12:00";
        } else if (normalized < 12) {
            return String.format("AM %d:00", normalized);
        } else if (normalized == 12) {
            return "PM 12:00";
        } else {
            return String.format("PM %d:00", normalized - 12);
        }
    }

    @Transactional
    public void confirmSchedule(Long teamId, Long userId, ConfirmScheduleRequest request) {
        //팀과 팀장 확인
        Team team = teamRepository.findByIdWithMembersAndUsers(teamId)
                .orElseThrow(() -> new CoreApiException(ErrorCode.TEAM_NOT_FOUND));

        boolean isLeader = team.getMembers().stream()
                .anyMatch(member -> member.getUser().getId().equals(userId) && member.getRole() == UserRole.LEADER);

        if (!isLeader) {
            throw new CoreApiException(ErrorCode.NOT_LEADER);
        }

        //스케줄 조회 및 확정 처리
        Schedule schedule = scheduleRepository.findByTeam_TeamId(teamId)
                .orElseThrow(() -> new CoreApiException(ErrorCode.SCHEDULE_NOT_FOUND));

        schedule.setConfirmedDate(request.getDate());
        schedule.setConfirmedHour(request.getHour());
        schedule.setConfirmed(true);
        scheduleRepository.save(schedule);

        //팀원에게 알림 보내기
        //String message = String.format("[%s]로부터 약속잡기가 확정되었습니다. 약속시간을 확인해보세요.", team.getTeamName());

        List<UserTeamManager> members = team.getMembers();
        for (UserTeamManager member : members) {
            Long memberUserId = member.getUser().getId();
            if (!memberUserId.equals(userId)) {
                notificationService.sendNotification(
                        member.getUser().getId(),
                        NotificationType.APPOINTMENT_REGISTRATION_COMPLETED,
                        team.getTeamName()
                );
            }
        }
    }


}


