package com.liveklass.query.application.controller;

import com.liveklass.command.domain.enumeration.EnrollmentStatus;
import com.liveklass.query.application.dto.LectureStudentResponse;
import com.liveklass.query.application.dto.MyEnrollmentResponse;
import com.liveklass.query.application.service.EnrollmentQueryService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EnrollmentQueryControllerTest {

    private MockMvc mockMvc;
    private EnrollmentQueryService enrollmentQueryService;

    @BeforeEach
    void setUp() {
        enrollmentQueryService = Mockito.mock(EnrollmentQueryService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new EnrollmentQueryController(enrollmentQueryService)).build();
    }

    @Test
    // ENR-LIST-001, ACC-AUTH-001
    // userId 헤더로 내 수강 신청 목록 조회 요청을 받고 목록 응답을 JSON 배열로 반환하는지 검증한다.
    void getMyEnrollments_returnsList() throws Exception {
        when(enrollmentQueryService.getMyEnrollments(1L, 0, 20)).thenReturn(new PageImpl<>(
                java.util.List.of(new MyEnrollmentResponse(
                10L,
                20L,
                "lecture-title",
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
                )),
                PageRequest.of(0, 20),
                1
        ));

        mockMvc.perform(get("/api/query/enrollments/me")
                        .header("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].enrollmentId").value(10L))
                .andExpect(jsonPath("$.content[0].enrollmentStatus").value("PENDING"))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(20));

        verify(enrollmentQueryService).getMyEnrollments(1L, 0, 20);
    }

    @Test
    // LEC-DETAIL-002
    // userId 헤더와 상태 필터로 강의별 수강생 목록 조회 요청 시 수강생 목록을 JSON 배열로 반환하는지 검증한다.
    void getLectureStudents_returnsList() throws Exception {
        when(enrollmentQueryService.getLectureStudents(1L, 20L, List.of(EnrollmentStatus.PENDING, EnrollmentStatus.CONFIRMED)))
                .thenReturn(java.util.List.of(new LectureStudentResponse(
                10L,
                1L,
                "student",
                EnrollmentStatus.CONFIRMED,
                LocalDateTime.of(2026, 5, 24, 10, 0),
                LocalDateTime.of(2026, 5, 24, 11, 0)
        )));

        mockMvc.perform(get("/api/query/lectures/20/students")
                        .header("userId", 1L)
                        .param("statuses", "PENDING,CONFIRMED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(1L))
                .andExpect(jsonPath("$[0].status").value("CONFIRMED"));

        verify(enrollmentQueryService).getLectureStudents(1L, 20L, List.of(EnrollmentStatus.PENDING, EnrollmentStatus.CONFIRMED));
    }
}
