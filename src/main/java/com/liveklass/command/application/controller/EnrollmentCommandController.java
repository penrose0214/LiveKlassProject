package com.liveklass.command.application.controller;

import com.liveklass.command.application.dto.ApplyEnrollmentResponse;
import com.liveklass.command.application.dto.CancelEnrollmentResponse;
import com.liveklass.command.application.dto.ConfirmPaymentResponse;
import com.liveklass.command.application.service.EnrollmentCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/command")
@RequiredArgsConstructor
public class EnrollmentCommandController {

    private final EnrollmentCommandService enrollmentCommandService;

    // ENR-APPLY-001
    // 강의 신청 요청
    @PostMapping("/lectures/{lectureId}/enrollments")
    public ResponseEntity<ApplyEnrollmentResponse> apply(
            @RequestHeader("userId") Long userId,
            @PathVariable Long lectureId
    ) {
        return ResponseEntity.ok(enrollmentCommandService.apply(userId, lectureId));
    }

    @PostMapping("/enrollments/{enrollmentId}/confirm-payment")
    public ResponseEntity<ConfirmPaymentResponse> confirmPayment(
            @RequestHeader("userId") Long userId,
            @PathVariable Long enrollmentId
    ) {
        return ResponseEntity.ok(enrollmentCommandService.confirmPayment(userId, enrollmentId));
    }

    // ENR-CANCEL-001
    // 수강 취소 요청
    @PostMapping("/enrollments/{enrollmentId}/cancel")
    public ResponseEntity<CancelEnrollmentResponse> cancel(
            @RequestHeader("userId") Long userId,
            @PathVariable Long enrollmentId
    ) {
        return ResponseEntity.ok(enrollmentCommandService.cancel(userId, enrollmentId));
    }
}
