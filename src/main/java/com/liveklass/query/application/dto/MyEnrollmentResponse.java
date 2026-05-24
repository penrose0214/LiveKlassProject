package com.liveklass.query.application.dto;

import com.liveklass.command.domain.enumeration.EnrollmentStatus;
import com.liveklass.command.domain.enumeration.LectureStatus;
import java.time.LocalDateTime;

public record MyEnrollmentResponse(
        Long enrollmentId,
        Long lectureId,
        String lectureTitle,
        String creatorName,
        LectureStatus lectureStatus,
        EnrollmentStatus enrollmentStatus,
        Long price,
        LocalDateTime appliedAt,
        LocalDateTime paymentDueAt,
        LocalDateTime confirmedAt,
        LocalDateTime cancelledAt,
        LocalDateTime lectureStartAt,
        LocalDateTime lectureEndAt
) {
}
