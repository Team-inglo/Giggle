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
@Table(name = "schedule")
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_time_id")
    private PartTime partTime;

    @Column(name = "start_at", nullable = false)
    LocalDateTime startAt;

    @Column(name = "end_at", nullable = false)
    LocalDateTime endAt;

    @Column(name = "created_at", nullable = false)
    LocalDateTime createdAt;

    @Builder
    public Schedule(User user, PartTime partTime, LocalDateTime startAt, LocalDateTime endAt) {
        this.user = user;
        this.partTime = partTime;
        this.startAt = startAt;
        this.endAt = endAt;
        this.createdAt = LocalDateTime.now();
    }

    public void updateSchedule(LocalDateTime startAt, LocalDateTime endAt) {
        this.startAt = startAt;
        this.endAt = endAt;
    }
}
