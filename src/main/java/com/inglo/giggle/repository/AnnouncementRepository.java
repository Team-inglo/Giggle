package com.inglo.giggle.repository;

import com.inglo.giggle.domain.Announcement;
import com.inglo.giggle.domain.Applicant;
import com.inglo.giggle.domain.Apply;
import com.inglo.giggle.domain.Owner;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    List<Announcement> findByOwner(Owner owner, Sort sort);

    List<Announcement> findByJobType(Owner owner, Sort sort);

    // 6371은 지구 반경을 km 단위로 표현한 값
    // 거리 순으로 정렬
    @Query("SELECT a FROM Announcement a WHERE " +
            "(6371 * acos(cos(radians(:userLat)) * cos(radians(a.owner.storeAddressX)) * " +
            "cos(radians(a.owner.storeAddressY) - radians(:userLng)) + sin(radians(:userLat)) * " +
            "sin(radians(a.owner.storeAddressX)))) <= :maxDistance")
    List<Announcement> findByOwnerLocationWithin(@Param("userAddressX") Double userAddressX,
                                                 @Param("userAddressY") Double userAddressY,
                                                 @Param("maxDistance") Double maxDistance);
}
