package com.project.icey.app.repository;

import com.project.icey.app.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
    //추후 필요시 추가
}
