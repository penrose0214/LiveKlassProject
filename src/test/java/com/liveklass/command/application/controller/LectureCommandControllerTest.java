package com.liveklass.command.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liveklass.command.application.dto.CreateLectureRequest;
import com.liveklass.command.application.dto.UpdateLectureRequest;
import com.liveklass.command.application.service.LectureCommandService;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LectureCommandControllerTest {

    private MockMvc mockMvc;
    private LectureCommandService lectureCommandService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        lectureCommandService = Mockito.mock(LectureCommandService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new LectureCommandController(lectureCommandService)).build();
        objectMapper = new ObjectMapper().findAndRegisterModules();
    }

    @Test
    void createLecture_callsServiceAndReturnsOk() throws Exception {
        CreateLectureRequest request = new CreateLectureRequest(
                "title",
                "description",
                10000L,
                30,
                LocalDateTime.of(2026, 5, 24, 10, 0),
                LocalDateTime.of(2026, 5, 25, 10, 0),
                LocalDateTime.of(2026, 6, 1, 10, 0),
                LocalDateTime.of(2026, 6, 30, 10, 0)
        );

        mockMvc.perform(post("/api/command/lectures")
                        .header("userId", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(lectureCommandService).createLecture(eq(1L), any(CreateLectureRequest.class));
    }

    @Test
    void updateLecture_callsServiceAndReturnsOk() throws Exception {
        UpdateLectureRequest request = new UpdateLectureRequest(
                "new-title",
                "new-description",
                20000L,
                20,
                LocalDateTime.of(2026, 5, 24, 10, 0),
                LocalDateTime.of(2026, 5, 25, 10, 0),
                LocalDateTime.of(2026, 6, 1, 10, 0),
                LocalDateTime.of(2026, 6, 30, 10, 0)
        );

        mockMvc.perform(put("/api/command/lectures/10")
                        .header("userId", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(lectureCommandService).updateLecture(eq(2L), eq(10L), any(UpdateLectureRequest.class));
    }

    @Test
    void openLecture_callsServiceAndReturnsOk() throws Exception {
        mockMvc.perform(post("/api/command/lectures/10/open")
                        .header("userId", 3L))
                .andExpect(status().isOk());

        verify(lectureCommandService).openLecture(3L, 10L);
    }

    @Test
    void closeLecture_callsServiceAndReturnsOk() throws Exception {
        mockMvc.perform(post("/api/command/lectures/10/close")
                        .header("userId", 4L))
                .andExpect(status().isOk());

        verify(lectureCommandService).closeLecture(4L, 10L);
    }
}
