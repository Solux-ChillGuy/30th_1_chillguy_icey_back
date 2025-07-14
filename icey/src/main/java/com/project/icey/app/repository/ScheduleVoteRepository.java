package com.project.icey.app.repository;

import com.project.icey.app.domain.ScheduleVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ScheduleVoteRepository extends JpaRepository<ScheduleVote, Long> {
    void deleteByVoterId(Long voterId);

    List<ScheduleVote> findByVoterId(Long voterId);
    @Query("SELECT COUNT(DISTINCT v.voter.id) FROM ScheduleVote v WHERE v.timeSlot.candidateDate.schedule.scheduleId = :scheduleId")
    Long countDistinctVotersByScheduleId(Long scheduleId);
}
