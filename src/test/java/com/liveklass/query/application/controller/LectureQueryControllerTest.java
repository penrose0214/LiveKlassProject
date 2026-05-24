package com.liveklass.query.application.controller;

import com.liveklass.command.domain.enumeration.LectureStatus;
import com.liveklass.query.application.dto.LectureDetailResponse;
import com.liveklass.query.application.dto.LectureSummaryResponse;
import com.liveklass.query.application.service.LectureQueryService;
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

class LectureQueryControllerTest {

    private MockMvc mockMvc;
    private LectureQueryService lectureQueryService;

    @BeforeEach
    void setUp() {
        lectureQueryService = Mockito.mock(LectureQueryService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new LectureQueryController(lectureQueryService)).build();
    }

    @Test
    void getLectures_returnsList() throws Exception {
        when(lectureQueryService.getLectures()).thenReturn(List.of(new LectureSummaryResponse(
                1L,
                2L,
                "creator",
                "title",
                10000L,
                30,
                LocalDateTime.of(2026, 5, 24, 10, 0),
                LocalDateTime.of(2026, 5, 25, 10, 0),
                LocalDateTime.of(2026, 6, 1, 10, 0),
                LocalDateTime.of(2026, 6, 30, 10, 0),
                LectureStatus.OPEN
        )));

        mockMvc.perform(get("/api/query/lectures"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].lectureId").value(1L))
                .andExpect(jsonPath("$[0].status").value("OPEN"));

        verify(lectureQueryService).getLectures();
    }

    @Test
    void getLectureDetail_returnsDetail() throws Exception {
        when(lectureQueryService.getLectureDetail(10L)).thenReturn(new LectureDetailResponse(
                10L,
                2L,
                "creator",
                "title",
                "description",
                10000L,
                30,
                LocalDateTime.of(2026, 5, 24, 10, 0),
                LocalDateTime.of(2026, 5, 25, 10, 0),
                LocalDateTime.of(2026, 6, 1, 10, 0),
                LocalDateTime.of(2026, 6, 30, 10, 0),
                LectureStatus.OPEN,
                5L,
                3L,
                2L
        ));

        mockMvc.perform(get("/api/query/lectures/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lectureId").value(10L))
                .andExpect(jsonPath("$.occupiedCount").value(5L));

        verify(lectureQueryService).getLectureDetail(10L);
    }
}
