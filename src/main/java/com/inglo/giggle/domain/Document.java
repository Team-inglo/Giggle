package com.inglo.giggle.domain;

import com.inglo.giggle.dto.type.DocumentType;
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
    private DocumentType type;

    @Column(name = "document_id", nullable = false)
    private String documentId;

    @Builder
    public Document(Apply apply, DocumentType type, String documentId){
        this.apply = apply;
        this.type = type;
        this.documentId = documentId;
    }

}
