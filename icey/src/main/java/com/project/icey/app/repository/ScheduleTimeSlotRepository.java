package com.project.icey.app.repository;

import com.project.icey.app.domain.ScheduleTimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;


public interface ScheduleTimeSlotRepository extends JpaRepository<ScheduleTimeSlot, Long> {

    // 특정 날짜 후보에 대한 모든 투표 조회 (조회용)
    List<ScheduleTimeSlot> findByCandidateDateId(Long candidateDateId);

    //날빠 후보 시간대에서 그 시간별로 찾기
    Optional<ScheduleTimeSlot> findByCandidateDateIdAndHour(Long candidateDateId, int hour);

}
