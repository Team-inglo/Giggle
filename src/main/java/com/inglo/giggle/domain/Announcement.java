package com.inglo.giggle.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "announcement")
public class Announcement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "announcement_id")
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "hourly_rate", nullable = false)
    private Integer hourlyRate;

    @Column(name = "start_working_at", nullable = false)
    private LocalDateTime startWorkingPeriod;

    @Column(name = "end_working_at", nullable = false)
    private LocalDateTime endWorkingPeriod;

    @Column(name = "working_days", nullable = false)
    private String workingDays;

    @Column(name = "working_start_time", nullable = false)
    private String workingStartTime;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "url", nullable = true)
    private String url = null;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public Announcement(String title, String address, Integer hourlyRate, LocalDateTime startWorkingPeriod, LocalDateTime endWorkingPeriod, String workingDays, String workingStartTime, String content, String url){
        this.title = title;
        this.address = address;
        this.hourlyRate = hourlyRate;
        this.startWorkingPeriod = startWorkingPeriod;
        this.endWorkingPeriod = endWorkingPeriod;
        this.workingDays = workingDays;
        this.workingStartTime = workingStartTime;
        this.content = content;
        this.url = url;
        this.createdAt = LocalDateTime.now();
    }
}
