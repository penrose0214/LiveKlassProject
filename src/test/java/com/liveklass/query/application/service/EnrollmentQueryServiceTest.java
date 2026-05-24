package com.liveklass.query.application.service;

import com.liveklass.command.domain.enumeration.EnrollmentStatus;
import com.liveklass.query.application.dto.LectureStudentResponse;
import com.liveklass.query.application.dto.MyEnrollmentResponse;
import com.liveklass.query.domain.repository.EnrollmentQueryRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnrollmentQueryServiceTest {

    @Mock
    private EnrollmentQueryRepository enrollmentQueryRepository;

    private EnrollmentQueryService enrollmentQueryService;

    @BeforeEach
    void setUp() {
        enrollmentQueryService = new EnrollmentQueryService(enrollmentQueryRepository);
    }

    @Test
    // ENR-LIST-001
    // 내 수강 신청 목록 조회 시 리포지토리 결과를 그대로 반환하는지 검증한다.
    void getMyEnrollments_returnsRepositoryResult() {
        List<MyEnrollmentResponse> responses = List.of(new MyEnrollmentResponse(
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
        ));
        when(enrollmentQueryRepository.findMyEnrollments(1L)).thenReturn(responses);

        List<MyEnrollmentResponse> result = enrollmentQueryService.getMyEnrollments(1L);

        assertThat(result).isEqualTo(responses);
        verify(enrollmentQueryRepository).findMyEnrollments(1L);
    }

    @Test
    // LEC-DETAIL-002
    // 강의별 수강생 목록 조회 시 리포지토리 결과를 그대로 반환하는지 검증한다.
    void getLectureStudents_returnsRepositoryResult() {
        List<LectureStudentResponse> responses = List.of(new LectureStudentResponse(
                10L,
                1L,
                "student",
                EnrollmentStatus.CONFIRMED,
                LocalDateTime.of(2026, 5, 24, 10, 0),
                LocalDateTime.of(2026, 5, 24, 11, 0)
        ));
        when(enrollmentQueryRepository.findConfirmedStudentsByLecture(20L)).thenReturn(responses);

        List<LectureStudentResponse> result = enrollmentQueryService.getLectureStudents(20L);

        assertThat(result).isEqualTo(responses);
        verify(enrollmentQueryRepository).findConfirmedStudentsByLecture(20L);
    }
}
