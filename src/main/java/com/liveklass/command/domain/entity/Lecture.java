package com.liveklass.command.domain.entity;


import com.liveklass.auth.AppUser;
import com.liveklass.command.domain.enumeration.LectureStatus;
import com.liveklass.common.exception.DomainValidationException;
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
        applyDetails(
                title,
                description,
                price,
                capacity,
                recruitmentStartAt,
                recruitmentEndAt,
                lectureStartAt,
                lectureEndAt
        );
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

    public void updateDetails(
            String title,
            String description,
            Long price,
            Integer capacity,
            LocalDateTime recruitmentStartAt,
            LocalDateTime recruitmentEndAt,
            LocalDateTime lectureStartAt,
            LocalDateTime lectureEndAt
    ) {
        applyDetails(
                title,
                description,
                price,
                capacity,
                recruitmentStartAt,
                recruitmentEndAt,
                lectureStartAt,
                lectureEndAt
        );
    }

    private void applyDetails(
            String title,
            String description,
            Long price,
            Integer capacity,
            LocalDateTime recruitmentStartAt,
            LocalDateTime recruitmentEndAt,
            LocalDateTime lectureStartAt,
            LocalDateTime lectureEndAt
    ) {
        validateDetails(
                title,
                description,
                price,
                capacity,
                recruitmentStartAt,
                recruitmentEndAt,
                lectureStartAt,
                lectureEndAt
        );

        this.title = title;
        this.description = description;
        this.price = price;
        this.capacity = capacity;
        this.recruitmentStartAt = recruitmentStartAt;
        this.recruitmentEndAt = recruitmentEndAt;
        this.lectureStartAt = lectureStartAt;
        this.lectureEndAt = lectureEndAt;
    }

    private void validateDetails(
            String title,
            String description,
            Long price,
            Integer capacity,
            LocalDateTime recruitmentStartAt,
            LocalDateTime recruitmentEndAt,
            LocalDateTime lectureStartAt,
            LocalDateTime lectureEndAt
    ) {
        if (title == null || title.isBlank()) {
            throw new DomainValidationException("강의 제목은 필수이며 공백일 수 없습니다.");
        }
        if (description == null || description.isBlank()) {
            throw new DomainValidationException("강의 설명은 필수이며 공백일 수 없습니다.");
        }
        if (price == null || price < 0) {
            throw new DomainValidationException("강의 가격은 0 이상이어야 합니다.");
        }
        if (capacity == null || capacity < 1) {
            throw new DomainValidationException("강의 정원은 1 이상이어야 합니다.");
        }
        if (recruitmentStartAt == null || recruitmentEndAt == null || lectureStartAt == null || lectureEndAt == null) {
            throw new DomainValidationException("강의 일정은 모두 입력되어야 합니다.");
        }
        if (recruitmentStartAt.isAfter(recruitmentEndAt)) {
            throw new DomainValidationException("모집 시작일은 모집 마감일보다 이전이거나 같아야 합니다.");
        }
        if (lectureStartAt.isAfter(lectureEndAt)) {
            throw new DomainValidationException("수강 시작일은 수강 종료일보다 이전이거나 같아야 합니다.");
        }
        if (recruitmentEndAt.isAfter(lectureStartAt)) {
            throw new DomainValidationException("모집 마감일은 수강 시작일보다 이전이거나 같아야 합니다.");
        }
    }

    public void changeStatus(LectureStatus status) {
        this.status = status;
    }
}
