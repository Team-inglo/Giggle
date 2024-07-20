package com.inglo.giggle.repository;

import com.inglo.giggle.domain.PartTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartTimeRepository extends JpaRepository<PartTime, Long> {
}
