package com.inglo.giggle.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "apply")
public class Apply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "apply_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id", nullable = false)
    private Applicant applicant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "announcement_id", nullable = false)
    private Announcement announcement;

    @Column(name = "step", nullable = false)
    private Integer step;

    @Column(name = "status", nullable = false)
    private Boolean status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "apply", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Document> documents = new ArrayList<>();

    @Builder
    public Apply(Applicant applicant, Announcement announcement, Integer step, Boolean status, LocalDateTime createdAt){
        this.applicant = applicant;
        this.announcement = announcement;
        this.step = step;
        this.status = status;
        this.createdAt = createdAt;
    }

    public void addStep(){
        this.step++;
    }

    public void advanceStep(Integer newStep){
        this.step = newStep;
    }

    public void advanceStatus() {
        this.status = false;
    }

}
