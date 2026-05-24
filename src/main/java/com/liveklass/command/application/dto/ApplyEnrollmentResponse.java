package com.liveklass.command.application.dto;

import com.liveklass.command.domain.enumeration.EnrollmentStatus;
import java.time.LocalDateTime;

public record ApplyEnrollmentResponse(
        Long enrollmentId,
        Long lectureId,
        Long userId,
        EnrollmentStatus status,
        LocalDateTime appliedAt,
        LocalDateTime paymentDueAt,
        LocalDateTime waitlistedAt
) {
}
