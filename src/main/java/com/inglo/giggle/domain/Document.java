package com.inglo.giggle.domain;

import com.inglo.giggle.dto.type.EDocumentType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "document")
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "document_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apply_id", nullable = false)
    private Apply apply;

    @Column(name = "type", nullable = false)
    private EDocumentType type;

    @Column(name = "document_type_id", nullable = false)
    private String documentId;

    @Column(name = "employer_email", nullable = true)
    private String employerEmail;

    @Column(name = "staff_email", nullable = true)
    private String staffEmail; // 교내유학생담당자 이메일



    @Builder
    public Document(Apply apply, EDocumentType type, String documentId){
        this.apply = apply;
        this.type = type;
        this.documentId = documentId;
    }

}
