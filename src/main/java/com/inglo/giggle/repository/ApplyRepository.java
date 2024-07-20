package com.inglo.giggle.repository;

import com.inglo.giggle.domain.Apply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplyRepository extends JpaRepository<Apply, Long> {
}
