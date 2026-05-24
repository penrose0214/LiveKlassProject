package com.liveklass.command.domain.repository;

public record EnrollValidation(
        Long occupiedCount,
        boolean hasActiveEnrollment
) {
}
