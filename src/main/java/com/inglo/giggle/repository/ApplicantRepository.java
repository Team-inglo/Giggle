package com.inglo.giggle.repository;

import com.inglo.giggle.domain.Applicant;
import com.inglo.giggle.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApplicantRepository extends JpaRepository<Applicant, Long> {
    Optional<Applicant> findByUser(User user);
}
