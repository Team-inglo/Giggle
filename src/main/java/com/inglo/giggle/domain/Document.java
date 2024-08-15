package com.inglo.giggle.domain;

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
    private Integer type;

    @Column(name = "document_id", nullable = false)
    private Long documentId;

    @Builder
    public Document(Apply apply, Integer type, Long templateId){
        this.apply = apply;
        this.type = type;
        this.documentId = templateId;
    }
}
