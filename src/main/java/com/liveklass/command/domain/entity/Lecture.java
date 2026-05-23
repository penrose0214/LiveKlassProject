package com.liveklass.command.domain.entity;


import com.liveklass.auth.AppUser;
import com.liveklass.command.domain.enumeration.LectureStatus;
import com.liveklass.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "lecture"
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Lecture extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lecture_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "creator_id", nullable = false)
    private AppUser creator;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Lob
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "price", nullable = false)
    private Long price;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @Column(name = "recruitment_start_at", nullable = false)
    private LocalDateTime recruitmentStartAt;

    @Column(name = "recruitment_end_at", nullable = false)
    private LocalDateTime recruitmentEndAt;

    @Column(name = "lecture_start_at", nullable = false)
    private LocalDateTime lectureStartAt;

    @Column(name = "lecture_end_at", nullable = false)
    private LocalDateTime lectureEndAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private LectureStatus status;

    public Lecture(
            AppUser creator,
            String title,
            String description,
            Long price,
            Integer capacity,
            LocalDateTime recruitmentStartAt,
            LocalDateTime recruitmentEndAt,
            LocalDateTime lectureStartAt,
            LocalDateTime lectureEndAt,
            LectureStatus status
    ) {
        this.creator = creator;
        this.title = title;
        this.description = description;
        this.price = price;
        this.capacity = capacity;
        this.recruitmentStartAt = recruitmentStartAt;
        this.recruitmentEndAt = recruitmentEndAt;
        this.lectureStartAt = lectureStartAt;
        this.lectureEndAt = lectureEndAt;
        this.status = status;
    }

    public static Lecture draft(
            AppUser creator,
            String title,
            String description,
            Long price,
            Integer capacity,
            LocalDateTime recruitmentStartAt,
            LocalDateTime recruitmentEndAt,
            LocalDateTime lectureStartAt,
            LocalDateTime lectureEndAt
    ) {
        return new Lecture(
                creator,
                title,
                description,
                price,
                capacity,
                recruitmentStartAt,
                recruitmentEndAt,
                lectureStartAt,
                lectureEndAt,
                LectureStatus.DRAFT
        );
    }
}
