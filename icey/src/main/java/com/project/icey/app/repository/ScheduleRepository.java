package com.project.icey.app.repository;

import com.project.icey.app.domain.Schedule;
import com.project.icey.app.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    boolean existsByTeam(Team team);
}
