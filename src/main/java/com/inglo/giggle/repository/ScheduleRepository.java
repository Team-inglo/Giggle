package com.inglo.giggle.repository;

import com.inglo.giggle.domain.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    @Query("SELECT s FROM Schedule s " +
            "JOIN FETCH s.partTime " +
            "WHERE s.user.id = :userId " +
            "AND (" +
            "   (s.startAt >= :startOfMonth AND s.startAt <= :endOfMonth) OR " +
            "   (s.endAt >= :startOfMonth AND s.endAt <= :endOfMonth) OR " +
            "   (s.startAt <= :startOfMonth AND s.endAt >= :endOfMonth)" +
            ")")
    List<Schedule> findAllByUserIdAndYearAndMonth(Long userId,
                                                  LocalDateTime startOfMonth,
                                                  LocalDateTime endOfMonth);

    @Query("SELECT s FROM Schedule s " +
            "JOIN FETCH s.partTime " +
            "WHERE s.partTime.id = :partTimeId " +
            "AND (" +
            "   (s.startAt >= :startOfMonth AND s.startAt <= :endOfMonth) OR " +
            "   (s.endAt >= :startOfMonth AND s.endAt <= :endOfMonth) OR " +
            "   (s.startAt <= :startOfMonth AND s.endAt >= :endOfMonth)" +
            ")")
    List<Schedule> findAllByPartTimeIdAndYearAndMonth(Long partTimeId,
                                                      LocalDateTime startOfMonth,
                                                      LocalDateTime endOfMonth);
}
