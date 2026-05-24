package com.liveklass.command.application.dto;

import java.time.LocalDateTime;

public record CreateLectureRequest(
        String title,
        String description,
        Long price,
        Integer capacity,
        LocalDateTime recruitmentStartAt,
        LocalDateTime recruitmentEndAt,
        LocalDateTime lectureStartAt,
        LocalDateTime lectureEndAt
) {
}
