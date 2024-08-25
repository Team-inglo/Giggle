package com.inglo.giggle.repository;

import com.inglo.giggle.domain.Applicant;
import com.inglo.giggle.domain.Apply;
import com.inglo.giggle.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplyRepository extends JpaRepository<Apply, Long> {
    List<Apply> findByApplicant(Applicant applicant);
}
