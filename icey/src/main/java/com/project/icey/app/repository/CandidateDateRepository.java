package com.project.icey.app.repository;

import com.project.icey.app.domain.CandidateDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CandidateDateRepository extends JpaRepository<CandidateDate, Long> {

    Optional<CandidateDate> findBySchedule_ScheduleIdAndDate(Long scheduleId, LocalDate date);

    List<CandidateDate> findBySchedule_ScheduleId(Long scheduleId);

    @Query("SELECT cd FROM CandidateDate cd LEFT JOIN FETCH cd.timeSlots WHERE cd.schedule.scheduleId = :scheduleId")
    List<CandidateDate> findWithTimeSlotsByScheduleId(Long scheduleId);
}

