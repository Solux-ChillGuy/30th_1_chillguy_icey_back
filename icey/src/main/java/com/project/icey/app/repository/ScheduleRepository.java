package com.project.icey.app.repository;

import com.project.icey.app.domain.Schedule;
import com.project.icey.app.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    boolean existsByTeam(Team team);
    Optional<Schedule> findByTeam_TeamId(Long teamId);
    boolean existsByTeam_TeamId(Long teamId);
}
