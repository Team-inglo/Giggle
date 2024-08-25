package com.inglo.giggle.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "work_day")
public class WorkDay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "work_day_id")
    Long id;

    @Column(name = "day", nullable = false)
    String day;

    @Column(name = "work_start_time", nullable = false)
    LocalTime workStartTime;

    @Column(name = "work_end_time", nullable = false)
    LocalTime workEndTime;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "announcement_id", nullable = false)
    private Announcement announcement;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public WorkDay(String day, LocalTime workStartTime, LocalTime workEndTime, Announcement announcement, LocalDateTime createdAt){
        this.day = day;
        this.workStartTime = workStartTime;
        this.workEndTime = workEndTime;
        this.announcement = announcement;
        this.createdAt = LocalDateTime.now();
    }
}
