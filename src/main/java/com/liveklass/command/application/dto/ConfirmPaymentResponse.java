package com.liveklass.command.application.dto;

import com.liveklass.command.domain.enumeration.EnrollmentStatus;
import java.time.LocalDateTime;

public record ConfirmPaymentResponse(
        Long enrollmentId,
        EnrollmentStatus status,
        LocalDateTime confirmedAt
) {
}
