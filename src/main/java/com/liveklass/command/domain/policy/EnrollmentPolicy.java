package com.liveklass.command.domain.policy;

import com.liveklass.command.domain.entity.Enrollment;
import com.liveklass.command.domain.enumeration.EnrollmentStatus;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class EnrollmentPolicy {

    public void validateApplicantNotCreator(Long creatorId, Long userId) {
        if (creatorId.equals(userId)) {
            throw new IllegalArgumentException("강의 개설자는 자신의 강의를 신청할 수 없습니다.");
        }
    }

    public void validateConfirmable(Enrollment enrollment, Long userId, LocalDateTime now) {
        if (!enrollment.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("본인 신청만 결제 확정할 수 있습니다.");
        }
        if (enrollment.getStatus() != EnrollmentStatus.PENDING) {
            throw new IllegalArgumentException("PENDING 상태의 신청만 결제 확정할 수 있습니다.");
        }
        if (enrollment.getPaymentDueAt() == null || now.isAfter(enrollment.getPaymentDueAt())) {
            throw new IllegalArgumentException("결제 마감 시간이 지났습니다.");
        }
    }

    public boolean releasesSeat(Enrollment enrollment) {
        return enrollment.getStatus() == EnrollmentStatus.PENDING
                || enrollment.getStatus() == EnrollmentStatus.CONFIRMED;
    }

    public void validateCancelable(Enrollment enrollment, Long userId, LocalDateTime now) {
        if (!enrollment.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("본인 신청만 취소할 수 있습니다.");
        }
        if (enrollment.getStatus() == EnrollmentStatus.CANCELLED) {
            throw new IllegalArgumentException("이미 취소된 신청입니다.");
        }
        if (enrollment.getStatus() == EnrollmentStatus.CONFIRMED) {
            if (enrollment.getConfirmedAt() == null) {
                throw new IllegalArgumentException("확정 시간이 없는 신청입니다.");
            }
            if (now.isAfter(enrollment.getConfirmedAt().plusDays(7))) {
                throw new IllegalArgumentException("결제 확정 후 7일이 지나 취소할 수 없습니다.");
            }
            if (!now.isBefore(enrollment.getLecture().getLectureStartAt())) {
                throw new IllegalArgumentException("강의 시작 이후에는 취소할 수 없습니다.");
            }
        }
    }
}
