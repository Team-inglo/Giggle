package com.inglo.giggle.repository;

import com.inglo.giggle.domain.Announcement;
import com.inglo.giggle.domain.Applicant;
import com.inglo.giggle.domain.Apply;
import com.inglo.giggle.domain.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    List<Announcement> findByOwner(Owner owner);
}
