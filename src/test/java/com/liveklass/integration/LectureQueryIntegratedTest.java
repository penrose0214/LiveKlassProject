package com.liveklass.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "/sql/query/lecture_setup.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/query/lecture_cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
class LectureQueryIntegratedTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    // LEC-LIST-001, LEC-LIST-002
    // 실제 DB 데이터를 기준으로 상태 필터, 집계 필드, 신청 가능 여부가 함께 반환되는지 검증한다.
    void getLectures_withStatusFilter_returnsFilteredPage() throws Exception {
        mockMvc.perform(get("/api/query/lectures")
                        .param("statuses", "OPEN")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].lectureId").value(10101))
                .andExpect(jsonPath("$.content[0].status").value("OPEN"))
                .andExpect(jsonPath("$.content[0].occupiedCount").value(2))
                .andExpect(jsonPath("$.content[0].confirmedCount").value(1))
                .andExpect(jsonPath("$.content[0].waitlistedCount").value(1))
                .andExpect(jsonPath("$.content[0].canApply").value(true))
                .andExpect(jsonPath("$.content[0].canWaitlist").value(false));
    }

    @Test
    // LEC-DETAIL-001
    // 실제 DB 기준 강의 상세 조회 시 현재 신청 인원 집계를 함께 반환하는지 검증한다.
    void getLectureDetail_returnsAggregatedCounts() throws Exception {
        mockMvc.perform(get("/api/query/lectures/{lectureId}", 10101))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lectureId").value(10101))
                .andExpect(jsonPath("$.title").value("OPEN 강의"))
                .andExpect(jsonPath("$.occupiedCount").value(2))
                .andExpect(jsonPath("$.confirmedCount").value(1))
                .andExpect(jsonPath("$.waitlistedCount").value(1));
    }
}
