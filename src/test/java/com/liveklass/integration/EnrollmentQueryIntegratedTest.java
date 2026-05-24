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
@Sql(scripts = "/sql/query/enrollment_setup.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/query/enrollment_cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
class EnrollmentQueryIntegratedTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    // LEC-DETAIL-002
    // 강의 제공자가 상태 필터를 전달하면 해당 상태의 수강생 목록만 실제 DB 기준으로 조회하는지 검증한다.
    void getLectureStudents_withStatuses_returnsFilteredStudents() throws Exception {
        mockMvc.perform(get("/api/query/lectures/{lectureId}/students", 10201)
                        .header("userId", 9301L)
                        .param("statuses", "PENDING,CONFIRMED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].status").value("CONFIRMED"))
                .andExpect(jsonPath("$[1].status").value("PENDING"));
    }

    @Test
    // LEC-DETAIL-002
    // 상태 필터를 생략하면 기본값으로 CONFIRMED 수강생만 조회하는지 실제 DB 기준으로 검증한다.
    void getLectureStudents_withoutStatuses_returnsConfirmedOnly() throws Exception {
        mockMvc.perform(get("/api/query/lectures/{lectureId}/students", 10201)
                        .header("userId", 9301L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("CONFIRMED"));
    }
}
