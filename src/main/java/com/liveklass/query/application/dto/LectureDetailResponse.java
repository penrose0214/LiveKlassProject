package com.liveklass.query.application.dto;

import com.liveklass.command.domain.enumeration.LectureStatus;
import java.time.LocalDateTime;

public record LectureDetailResponse(
        Long lectureId,
        Long creatorId,
        String creatorName,
        String title,
        String description,
        Long price,
        Integer capacity,
        LocalDateTime recruitmentStartAt,
        LocalDateTime recruitmentEndAt,
        LocalDateTime lectureStartAt,
        LocalDateTime lectureEndAt,
        LectureStatus status,
        Long occupiedCount,
        Long confirmedCount,
        Long waitlistedCount
) {
}
