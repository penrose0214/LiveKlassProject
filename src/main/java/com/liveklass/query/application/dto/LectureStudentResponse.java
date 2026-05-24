package com.liveklass.query.application.dto;

import com.liveklass.command.domain.enumeration.EnrollmentStatus;
import java.time.LocalDateTime;

public record LectureStudentResponse(
        Long enrollmentId,
        Long userId,
        String userName,
        EnrollmentStatus status,
        LocalDateTime appliedAt,
        LocalDateTime confirmedAt
) {
}
