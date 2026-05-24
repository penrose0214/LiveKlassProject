package com.liveklass.command.application.service;

import com.liveklass.auth.AppUser;
import com.liveklass.command.application.dto.ApplyEnrollmentResponse;
import com.liveklass.command.application.dto.CancelEnrollmentResponse;
import com.liveklass.command.application.dto.ConfirmPaymentResponse;
import com.liveklass.command.domain.entity.Enrollment;
import com.liveklass.command.domain.entity.Lecture;
import com.liveklass.command.domain.enumeration.EnrollmentStatus;
import com.liveklass.command.domain.policy.CapacityPolicy;
import com.liveklass.command.domain.policy.EnrollmentPolicy;
import com.liveklass.command.domain.policy.LecturePolicy;
import com.liveklass.command.domain.policy.WaitlistPolicy;
import com.liveklass.command.domain.repository.EnrollmentRepository;
import com.liveklass.command.domain.repository.LectureRepository;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class EnrollmentCommandService {

    private static final EnumSet<EnrollmentStatus> ACTIVE_STATUSES =
            EnumSet.of(EnrollmentStatus.WAITLISTED, EnrollmentStatus.PENDING, EnrollmentStatus.CONFIRMED);
    private static final EnumSet<EnrollmentStatus> OCCUPIED_STATUSES =
            EnumSet.of(EnrollmentStatus.PENDING, EnrollmentStatus.CONFIRMED);

    private final EnrollmentRepository enrollmentRepository;
    private final LectureRepository lectureRepository;
    private final LecturePolicy lecturePolicy;
    private final EnrollmentPolicy enrollmentPolicy;
    private final CapacityPolicy capacityPolicy;
    private final WaitlistPolicy waitlistPolicy;
    private final EntityManager entityManager;

    public ApplyEnrollmentResponse apply(Long userId, Long lectureId) {
        LocalDateTime now = LocalDateTime.now();
        Lecture lecture = lectureRepository.findByIdForUpdate(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("강의를 찾을 수 없습니다. id=" + lectureId));

        lecturePolicy.validateRecruitmentOpen(lecture, now);
        enrollmentPolicy.validateApplicantNotCreator(lecture.getCreator().getId(), userId);

        boolean alreadyApplied = enrollmentRepository.existsActiveEnrollment(lectureId, userId, ACTIVE_STATUSES);
        if (alreadyApplied) {
            throw new IllegalArgumentException("이미 활성 신청이 존재합니다.");
        }

        long occupiedCount = enrollmentRepository.countOccupiedByLectureId(lectureId, OCCUPIED_STATUSES);
        AppUser user = entityManager.getReference(AppUser.class, userId);

        Enrollment enrollment;
        if (capacityPolicy.hasAvailableSeat(lecture, occupiedCount)) {
            enrollment = Enrollment.pending(lecture, user, now, now.plusDays(1));
        } else {
            enrollment = Enrollment.waitlisted(lecture, user, now);
        }

        Enrollment saved = enrollmentRepository.save(enrollment);
        return new ApplyEnrollmentResponse(
                saved.getId(),
                lectureId,
                userId,
                saved.getStatus(),
                saved.getAppliedAt(),
                saved.getPaymentDueAt(),
                saved.getWaitlistedAt()
        );
    }

    public ConfirmPaymentResponse confirmPayment(Long userId, Long enrollmentId) {
        LocalDateTime now = LocalDateTime.now();
        Enrollment enrollment = enrollmentRepository.findByIdForUpdate(enrollmentId)
                .orElseThrow(() -> new IllegalArgumentException("신청 정보를 찾을 수 없습니다. id=" + enrollmentId));

        enrollmentPolicy.validateConfirmable(enrollment, userId, now);
        enrollment.confirm(now);

        return new ConfirmPaymentResponse(
                enrollment.getId(),
                enrollment.getStatus(),
                enrollment.getConfirmedAt()
        );
    }

    public CancelEnrollmentResponse cancel(Long userId, Long enrollmentId) {
        LocalDateTime now = LocalDateTime.now();
        Enrollment enrollment = enrollmentRepository.findByIdForUpdate(enrollmentId)
                .orElseThrow(() -> new IllegalArgumentException("신청 정보를 찾을 수 없습니다. id=" + enrollmentId));

        Lecture lecture = lectureRepository.findByIdForUpdate(enrollment.getLecture().getId())
                .orElseThrow(() -> new IllegalArgumentException("강의를 찾을 수 없습니다. id=" + enrollment.getLecture().getId()));

        boolean releasesSeat = enrollmentPolicy.releasesSeat(enrollment);
        enrollmentPolicy.validateCancelable(enrollment, userId, now);
        enrollment.cancel(now);

        if (releasesSeat) {
            promoteFirstWaitlistedIfNeeded(lecture, now);
        }

        return new CancelEnrollmentResponse(
                enrollment.getId(),
                enrollment.getStatus(),
                enrollment.getCancelledAt()
        );
    }

    private void promoteFirstWaitlistedIfNeeded(Lecture lecture, LocalDateTime now) {
        if (!waitlistPolicy.canPromote(lecture, now)) {
            return;
        }

        List<Enrollment> waitlisted = enrollmentRepository.findFirstWaitlistedForUpdate(
                lecture.getId(),
                EnrollmentStatus.WAITLISTED,
                PageRequest.of(0, 1)
        );
        if (waitlisted.isEmpty()) {
            return;
        }

        Enrollment firstWaitlisted = waitlisted.get(0);
        waitlistPolicy.validateWaitlisted(firstWaitlisted);
        firstWaitlisted.promoteToPending(now.plusDays(1));
    }
}
