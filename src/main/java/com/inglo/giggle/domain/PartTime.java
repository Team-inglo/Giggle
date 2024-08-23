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
@Table(name = "part_time")
public class PartTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "part_time_id")
    private Long id;

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(name = "part_time_name", nullable = false)
    private String partTimeName;

    @Column(name = "hourly_rate", nullable = false)
    private Integer hourlyRate;

    @Column(name = "color", nullable = false)
    private String color = "#ff0000";

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public PartTime(User user, String partTimeName, Integer hourlyRate, String color){
        this.user = user;
        this.partTimeName = partTimeName;
        this.hourlyRate = hourlyRate;
        this.color = color;
        this.createdAt = LocalDateTime.now();
    }

    public void updatePartTime(String partTimeName, Integer hourlyRate, String color){
        if(partTimeName != null)
            this.partTimeName = partTimeName;
        if(hourlyRate != null)
            this.hourlyRate = hourlyRate;
        if(color != null)
            this.color = color;
    }
}
