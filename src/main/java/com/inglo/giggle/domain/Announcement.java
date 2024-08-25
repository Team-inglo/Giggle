package com.inglo.giggle.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Column(name = "work_start_date", nullable = false)
    private LocalDate workStartDate;

    @Column(name = "dead_line", nullable = false)
    private LocalDate deadLine;

    @Column(name = "working_period", nullable = false)
    private Integer workingPeriod;

    @Column(name = "age", nullable = false)
    private Integer age;

    @Column(name = "gender", nullable = false)
    private String gender;

    @Column(name = "education", nullable = false)
    private String education;

    @Column(name = "address_name", nullable = false)
    private String addressName;

    @Column(name = "address_x", nullable = false)
    private float addressX;

    @Column(name = "address_y", nullable = false)
    private float addressY;

    @Column(name = "number_recruited", nullable = false)
    private Integer numberRecruited;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "announcement", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<WorkDay> workDays = new ArrayList<>(); // 시간

    @Builder
    public Announcement(String title, String address, Integer hourlyRate, LocalDate workStartDate, LocalDate deadLine, Integer workingPeriod, Integer age, String gender, String education, String addressName, float addressX, float addressY, Integer numberRecruited, String content, List<WorkDay> workDays){
        this.title = title;
        this.address = address;
        this.hourlyRate = hourlyRate;
        this.workStartDate = workStartDate;
        this.deadLine = deadLine;
        this.workingPeriod = workingPeriod;
        this.age = age;
        this.gender = gender;
        this.education = education;
        this.addressName = addressName;
        this.addressX = addressX;
        this.addressY = addressY;
        this.numberRecruited = numberRecruited;
        this.content = content;
        this.workDays = workDays;
        this.createdAt = LocalDateTime.now();
    }
}
