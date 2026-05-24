package com.liveklass.query.application.service;

import com.liveklass.auth.AppUser;
import com.liveklass.command.domain.entity.Lecture;
import com.liveklass.command.domain.enumeration.EnrollmentStatus;
import com.liveklass.command.domain.enumeration.LectureStatus;
import com.liveklass.command.domain.policy.LecturePolicy;
import com.liveklass.command.domain.repository.LectureRepository;
import com.liveklass.query.application.dto.LectureStudentResponse;
import com.liveklass.query.application.dto.MyEnrollmentResponse;
import com.liveklass.query.domain.repository.EnrollmentQueryRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnrollmentQueryServiceTest {

    @Mock
    private EnrollmentQueryRepository enrollmentQueryRepository;
    @Mock
    private LectureRepository lectureRepository;
    @Mock
    private LecturePolicy lecturePolicy;

    private EnrollmentQueryService enrollmentQueryService;

    @BeforeEach
    void setUp() {
        enrollmentQueryService = new EnrollmentQueryService(enrollmentQueryRepository, lectureRepository, lecturePolicy);
    }

    @Test
    // ENR-LIST-001
    // 내 수강 신청 목록 조회 시 리포지토리 결과를 그대로 반환하는지 검증한다.
    void getMyEnrollments_returnsRepositoryResult() {
        Page<MyEnrollmentResponse> responses = new PageImpl<>(java.util.List.of(new MyEnrollmentResponse(
                10L,
                20L,
                "lecture",
                "creator",
                null,
                EnrollmentStatus.PENDING,
                10000L,
                LocalDateTime.of(2026, 5, 24, 10, 0),
                LocalDateTime.of(2026, 5, 25, 10, 0),
                null,
                null,
                LocalDateTime.of(2026, 6, 1, 10, 0),
                LocalDateTime.of(2026, 6, 30, 10, 0)
        )), PageRequest.of(0, 20), 1);
        when(enrollmentQueryRepository.findMyEnrollments(1L, PageRequest.of(0, 20))).thenReturn(responses);

        Page<MyEnrollmentResponse> result = enrollmentQueryService.getMyEnrollments(1L, 0, 20);

        assertThat(result).isEqualTo(responses);
        verify(enrollmentQueryRepository).findMyEnrollments(1L, PageRequest.of(0, 20));
    }

    @Test
    // LEC-DETAIL-002
    // 강의 제공자 요청으로 상태 필터 없이 조회하면 CONFIRMED 수강생 목록을 반환하는지 검증한다.
    void getLectureStudents_withoutStatuses_returnsConfirmedStudents() {
        Lecture lecture = lecture(20L, 1L);
        java.util.List<LectureStudentResponse> responses = java.util.List.of(new LectureStudentResponse(
                10L,
                1L,
                "student",
                EnrollmentStatus.CONFIRMED,
                LocalDateTime.of(2026, 5, 24, 10, 0),
                LocalDateTime.of(2026, 5, 24, 11, 0)
        ));
        when(lectureRepository.findById(20L)).thenReturn(Optional.of(lecture));
        when(enrollmentQueryRepository.findStudentsByLectureAndStatuses(20L, List.of(EnrollmentStatus.CONFIRMED)))
                .thenReturn(responses);

        List<LectureStudentResponse> result = enrollmentQueryService.getLectureStudents(1L, 20L, null);

        assertThat(result).isEqualTo(responses);
        verify(lecturePolicy).validateCreator(lecture, 1L);
        verify(enrollmentQueryRepository).findStudentsByLectureAndStatuses(20L, List.of(EnrollmentStatus.CONFIRMED));
    }

    @Test
    // LEC-DETAIL-002
    // 상태 필터를 전달하면 해당 상태들로 강의별 수강생 목록을 조회하는지 검증한다.
    void getLectureStudents_withStatuses_usesRequestedStatuses() {
        Lecture lecture = lecture(20L, 1L);
        List<EnrollmentStatus> statuses = List.of(EnrollmentStatus.PENDING, EnrollmentStatus.WAITLISTED);
        when(lectureRepository.findById(20L)).thenReturn(Optional.of(lecture));
        when(enrollmentQueryRepository.findStudentsByLectureAndStatuses(20L, statuses)).thenReturn(List.of());

        List<LectureStudentResponse> result = enrollmentQueryService.getLectureStudents(1L, 20L, statuses);

        assertThat(result).isEmpty();
        verify(lecturePolicy).validateCreator(lecture, 1L);
        verify(enrollmentQueryRepository).findStudentsByLectureAndStatuses(20L, statuses);
    }

    private Lecture lecture(Long lectureId, Long creatorId) {
        AppUser creator = new AppUser("creator");
        org.springframework.test.util.ReflectionTestUtils.setField(creator, "id", creatorId);
        Lecture lecture = Lecture.draft(
                creator,
                "title",
                "description",
                10000L,
                30,
                LocalDateTime.of(2026, 5, 24, 10, 0),
                LocalDateTime.of(2026, 5, 25, 10, 0),
                LocalDateTime.of(2026, 6, 1, 10, 0),
                LocalDateTime.of(2026, 6, 30, 10, 0)
        );
        org.springframework.test.util.ReflectionTestUtils.setField(lecture, "id", lectureId);
        lecture.changeStatus(LectureStatus.OPEN);
        return lecture;
    }
}
