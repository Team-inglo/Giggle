package com.inglo.giggle.repository;

import com.inglo.giggle.domain.Apply;
import com.inglo.giggle.domain.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    // document id와 일치하는 최신순의 객체
    Optional<Document> findByDocumentId(String documentId);

    List<Document> findByApply(Apply apply);
}
