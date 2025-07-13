package com.project.icey.app.repository;

import com.project.icey.app.domain.ScheduleVote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleVoteRepository extends JpaRepository<ScheduleVote, Long> {
    void deleteByVoterId(Long voterId);

    List<ScheduleVote> findByVoterId(Long voterId);

}
