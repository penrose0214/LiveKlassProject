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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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
    // LEC-LIST-001, LEC-LIST-002
    // 강의 목록 조회 요청 시 강의 목록 응답을 JSON 배열로 반환하는지 검증한다.
    void getLectures_returnsList() throws Exception {
        when(lectureQueryService.getLectures(null, 0, 20)).thenReturn(new PageImpl<>(
                java.util.List.of(new LectureSummaryResponse(
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
                LectureStatus.OPEN,
                5L,
                3L,
                2L,
                true,
                false
                )),
                PageRequest.of(0, 20),
                1
        ));

        mockMvc.perform(get("/api/query/lectures"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].lectureId").value(1L))
                .andExpect(jsonPath("$.content[0].status").value("OPEN"))
                .andExpect(jsonPath("$.content[0].occupiedCount").value(5L))
                .andExpect(jsonPath("$.content[0].canApply").value(true))
                .andExpect(jsonPath("$.content[0].canWaitlist").value(false))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(20));

        verify(lectureQueryService).getLectures(null, 0, 20);
    }

    @Test
    // LEC-LIST-002
    // 상태 필터를 함께 전달하면 해당 상태 목록이 서비스로 전달되는지 검증한다.
    void getLectures_withStatuses_passesFilter() throws Exception {
        when(lectureQueryService.getLectures(List.of(LectureStatus.OPEN, LectureStatus.CLOSED), 0, 20))
                .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 20), 0));

        mockMvc.perform(get("/api/query/lectures")
                        .param("statuses", "OPEN,CLOSED"))
                .andExpect(status().isOk());

        verify(lectureQueryService).getLectures(List.of(LectureStatus.OPEN, LectureStatus.CLOSED), 0, 20);
    }

    @Test
    // LEC-DETAIL-001
    // lectureId 경로값으로 강의 상세 조회 요청 시 상세 정보를 JSON으로 반환하는지 검증한다.
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
