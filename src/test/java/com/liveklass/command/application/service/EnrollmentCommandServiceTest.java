package com.liveklass.command.application.service;

import com.liveklass.auth.AppUser;
import com.liveklass.command.application.dto.ApplyEnrollmentResponse;
import com.liveklass.command.application.dto.CancelEnrollmentResponse;
import com.liveklass.command.application.dto.ConfirmPaymentResponse;
import com.liveklass.command.domain.entity.Enrollment;
import com.liveklass.command.domain.entity.Lecture;
import com.liveklass.command.domain.enumeration.EnrollmentStatus;
import com.liveklass.command.domain.enumeration.LectureStatus;
import com.liveklass.command.domain.policy.CapacityPolicy;
import com.liveklass.command.domain.policy.EnrollmentPolicy;
import com.liveklass.command.domain.policy.LecturePolicy;
import com.liveklass.command.domain.policy.WaitlistPolicy;
import com.liveklass.command.domain.repository.EnrollmentRepository;
import com.liveklass.command.domain.repository.LectureRepository;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnrollmentCommandServiceTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;
    @Mock
    private LectureRepository lectureRepository;
    @Mock
    private LecturePolicy lecturePolicy;
    @Mock
    private EnrollmentPolicy enrollmentPolicy;
    @Mock
    private CapacityPolicy capacityPolicy;
    @Mock
    private WaitlistPolicy waitlistPolicy;
    @Mock
    private EntityManager entityManager;

    private EnrollmentCommandService enrollmentCommandService;

    @BeforeEach
    void setUp() {
        enrollmentCommandService = new EnrollmentCommandService(
                enrollmentRepository,
                lectureRepository,
                lecturePolicy,
                enrollmentPolicy,
                capacityPolicy,
                waitlistPolicy,
                entityManager
        );
    }

    @Test
    // ENR-APPLY-001, ENR-PAY-001, CAP-RULE-001, CAP-RULE-002, CAP-CONC-001, CAP-CONC-002
    // 잔여 좌석이 있을 때 수강 신청을 PENDING으로 생성하고 결제 마감 시각을 설정하는지 검증한다.
    void apply_whenSeatAvailable_createsPendingEnrollment() {
        Lecture lecture = openLecture();
        AppUser applicant = user(2L, "student");
        when(lectureRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(lecture));
        when(enrollmentRepository.existsActiveEnrollment(eq(10L), eq(2L), anyCollection())).thenReturn(false);
        when(enrollmentRepository.countOccupiedByLectureId(eq(10L), anyCollection())).thenReturn(1L);
        when(capacityPolicy.hasAvailableSeat(lecture, 1L)).thenReturn(true);
        when(entityManager.getReference(AppUser.class, 2L)).thenReturn(applicant);
        when(enrollmentRepository.save(any(Enrollment.class))).thenAnswer(invocation -> {
            Enrollment enrollment = invocation.getArgument(0);
            ReflectionTestUtils.setField(enrollment, "id", 100L);
            return enrollment;
        });

        ApplyEnrollmentResponse response = enrollmentCommandService.apply(2L, 10L);

        ArgumentCaptor<Enrollment> captor = ArgumentCaptor.forClass(Enrollment.class);
        verify(enrollmentRepository).save(captor.capture());
        Enrollment saved = captor.getValue();
        assertThat(saved.getStatus()).isEqualTo(EnrollmentStatus.PENDING);
        assertThat(saved.getPaymentDueAt()).isNotNull();
        assertThat(response.enrollmentId()).isEqualTo(100L);
        assertThat(response.status()).isEqualTo(EnrollmentStatus.PENDING);
    }

    @Test
    // ENR-APPLY-001, ENR-WAIT-001, CAP-RULE-001, CAP-RULE-002, CAP-CONC-001, CAP-CONC-002
    // 정원이 가득 찼을 때 수강 신청을 실패시키지 않고 WAITLISTED로 생성하는지 검증한다.
    void apply_whenFull_createsWaitlistedEnrollment() {
        Lecture lecture = openLecture();
        AppUser applicant = user(2L, "student");
        when(lectureRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(lecture));
        when(enrollmentRepository.existsActiveEnrollment(eq(10L), eq(2L), anyCollection())).thenReturn(false);
        when(enrollmentRepository.countOccupiedByLectureId(eq(10L), anyCollection())).thenReturn(30L);
        when(capacityPolicy.hasAvailableSeat(lecture, 30L)).thenReturn(false);
        when(entityManager.getReference(AppUser.class, 2L)).thenReturn(applicant);
        when(enrollmentRepository.save(any(Enrollment.class))).thenAnswer(invocation -> {
            Enrollment enrollment = invocation.getArgument(0);
            ReflectionTestUtils.setField(enrollment, "id", 101L);
            return enrollment;
        });

        ApplyEnrollmentResponse response = enrollmentCommandService.apply(2L, 10L);

        assertThat(response.status()).isEqualTo(EnrollmentStatus.WAITLISTED);
        assertThat(response.waitlistedAt()).isNotNull();
    }

    @Test
    // ENR-APPLY-003, CAP-RULE-002
    // 동일 강의에 활성 신청이 이미 존재하면 중복 신청을 거부하는지 검증한다.
    void apply_whenDuplicateEnrollmentExists_throws() {
        Lecture lecture = openLecture();
        when(lectureRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(lecture));
        when(enrollmentRepository.existsActiveEnrollment(eq(10L), eq(2L), anyCollection())).thenReturn(true);

        assertThatThrownBy(() -> enrollmentCommandService.apply(2L, 10L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 활성 신청");
    }

    @Test
    // ENR-PAY-002
    // PENDING 신청의 결제 확정 시 상태를 CONFIRMED로 바꾸고 confirmedAt을 기록하는지 검증한다.
    void confirmPayment_confirmsEnrollment() {
        Enrollment enrollment = pendingEnrollment(openLecture(), user(2L, "student"));
        ReflectionTestUtils.setField(enrollment, "id", 200L);
        when(enrollmentRepository.findByIdForUpdate(200L)).thenReturn(Optional.of(enrollment));

        ConfirmPaymentResponse response = enrollmentCommandService.confirmPayment(2L, 200L);

        verify(enrollmentPolicy).validateConfirmable(eq(enrollment), eq(2L), any(LocalDateTime.class));
        assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.CONFIRMED);
        assertThat(response.status()).isEqualTo(EnrollmentStatus.CONFIRMED);
        assertThat(response.confirmedAt()).isNotNull();
    }

    @Test
    // ENR-CANCEL-001, ENR-WAIT-002, CAP-CONC-001
    // 좌석을 점유하던 신청이 취소되면 대기열 첫 번째 신청을 PENDING으로 승격하는지 검증한다.
    void cancel_whenSeatReleased_promotesFirstWaitlisted() {
        Lecture lecture = openLecture();
        Enrollment target = pendingEnrollment(lecture, user(2L, "student"));
        ReflectionTestUtils.setField(target, "id", 300L);
        Enrollment waitlisted = waitlistedEnrollment(lecture, user(3L, "waitlisted"));
        when(enrollmentRepository.findByIdForUpdate(300L)).thenReturn(Optional.of(target));
        when(lectureRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(lecture));
        when(enrollmentPolicy.releasesSeat(target)).thenReturn(true);
        when(waitlistPolicy.canPromote(eq(lecture), any(LocalDateTime.class))).thenReturn(true);
        when(enrollmentRepository.findFirstWaitlistedForUpdate(eq(10L), eq(EnrollmentStatus.WAITLISTED), any(Pageable.class)))
                .thenReturn(List.of(waitlisted));

        CancelEnrollmentResponse response = enrollmentCommandService.cancel(2L, 300L);

        verify(enrollmentPolicy).validateCancelable(eq(target), eq(2L), any(LocalDateTime.class));
        verify(waitlistPolicy).validateWaitlisted(waitlisted);
        assertThat(target.getStatus()).isEqualTo(EnrollmentStatus.CANCELLED);
        assertThat(waitlisted.getStatus()).isEqualTo(EnrollmentStatus.PENDING);
        assertThat(response.status()).isEqualTo(EnrollmentStatus.CANCELLED);
    }

    @Test
    // ENR-CANCEL-001
    // 좌석을 점유하지 않던 신청 취소 시에는 대기열 승격이 발생하지 않는지 검증한다.
    void cancel_whenSeatNotReleased_doesNotPromoteWaitlist() {
        Lecture lecture = openLecture();
        Enrollment target = waitlistedEnrollment(lecture, user(2L, "student"));
        ReflectionTestUtils.setField(target, "id", 301L);
        when(enrollmentRepository.findByIdForUpdate(301L)).thenReturn(Optional.of(target));
        when(lectureRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(lecture));
        when(enrollmentPolicy.releasesSeat(target)).thenReturn(false);

        enrollmentCommandService.cancel(2L, 301L);

        verify(enrollmentRepository, never()).findFirstWaitlistedForUpdate(anyLong(), any(), any(Pageable.class));
        assertThat(target.getStatus()).isEqualTo(EnrollmentStatus.CANCELLED);
    }

    private Lecture openLecture() {
        AppUser creator = user(1L, "creator");
        Lecture lecture = Lecture.draft(
                creator,
                "title",
                "description",
                10000L,
                30,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(10),
                LocalDateTime.now().plusDays(20)
        );
        ReflectionTestUtils.setField(lecture, "id", 10L);
        lecture.changeStatus(LectureStatus.OPEN);
        return lecture;
    }

    private AppUser user(Long id, String name) {
        AppUser user = new AppUser(name);
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }

    private Enrollment pendingEnrollment(Lecture lecture, AppUser user) {
        Enrollment enrollment = Enrollment.pending(lecture, user, LocalDateTime.now(), LocalDateTime.now().plusDays(1));
        ReflectionTestUtils.setField(enrollment, "id", 300L);
        return enrollment;
    }

    private Enrollment waitlistedEnrollment(Lecture lecture, AppUser user) {
        Enrollment enrollment = Enrollment.waitlisted(lecture, user, LocalDateTime.now());
        ReflectionTestUtils.setField(enrollment, "id", 301L);
        return enrollment;
    }
}
