package com.inglo.giggle.repository;

import com.inglo.giggle.domain.Applicant;
import com.inglo.giggle.domain.ApplicantFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApplicantFileRepository extends JpaRepository<ApplicantFile, Long> {
    Optional<ApplicantFile> findByApplicant(Applicant applicant);
}
