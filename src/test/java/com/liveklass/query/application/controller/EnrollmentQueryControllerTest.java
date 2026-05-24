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
    void getMyEnrollments_returnsList() throws Exception {
        when(enrollmentQueryService.getMyEnrollments(1L)).thenReturn(List.of(new MyEnrollmentResponse(
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
        )));

        mockMvc.perform(get("/api/query/enrollments/me")
                        .header("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].enrollmentId").value(10L))
                .andExpect(jsonPath("$[0].enrollmentStatus").value("PENDING"));

        verify(enrollmentQueryService).getMyEnrollments(1L);
    }

    @Test
    void getLectureStudents_returnsList() throws Exception {
        when(enrollmentQueryService.getLectureStudents(20L)).thenReturn(List.of(new LectureStudentResponse(
                10L,
                1L,
                "student",
                EnrollmentStatus.CONFIRMED,
                LocalDateTime.of(2026, 5, 24, 10, 0),
                LocalDateTime.of(2026, 5, 24, 11, 0)
        )));

        mockMvc.perform(get("/api/query/lectures/20/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(1L))
                .andExpect(jsonPath("$[0].status").value("CONFIRMED"));

        verify(enrollmentQueryService).getLectureStudents(20L);
    }
}
