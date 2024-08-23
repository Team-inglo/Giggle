package com.inglo.giggle.repository;

import com.inglo.giggle.domain.Owner;
import com.inglo.giggle.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OwnerRepository extends JpaRepository<Owner, Long> {
    Optional<Owner> findByUser(User user);
}
