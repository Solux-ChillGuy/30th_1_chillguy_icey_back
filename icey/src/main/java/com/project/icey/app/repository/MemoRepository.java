package com.project.icey.app.repository;

import com.project.icey.app.domain.Memo;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface MemoRepository extends JpaRepository<Memo, Long> {

    @Query("""

        select m from Memo m
        join fetch m.utm u
        where u.team.teamId = :teamId
        order by m.createdAt asc
""")
    List<Memo> findByTeamId(@Param("teamId") Long teamId);

    @Query("select count(m) from Memo m where m.utm.team.teamId = :teamId")  //역시 teamId
    long countByTeamId(@Param("teamId") Long teamId);

}
