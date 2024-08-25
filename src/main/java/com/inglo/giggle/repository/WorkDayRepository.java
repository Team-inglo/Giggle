package com.inglo.giggle.repository;

import com.inglo.giggle.domain.Announcement;
import com.inglo.giggle.domain.WorkDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkDayRepository extends JpaRepository<WorkDay, Long> {
}
