package com.project.icey.app.repository;

import com.project.icey.app.domain.ScheduleVote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleVoteRepository extends JpaRepository<ScheduleVote, Long> {
    void deleteByVoterId(Long voterId);
}
