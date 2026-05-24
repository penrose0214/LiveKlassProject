package com.liveklass.command.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liveklass.command.application.dto.ApplyEnrollmentResponse;
import com.liveklass.command.application.dto.CancelEnrollmentResponse;
import com.liveklass.command.application.dto.ConfirmPaymentResponse;
import com.liveklass.command.application.service.EnrollmentCommandService;
import com.liveklass.command.domain.enumeration.EnrollmentStatus;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EnrollmentCommandControllerTest {

    private MockMvc mockMvc;
    private EnrollmentCommandService enrollmentCommandService;

    @BeforeEach
    void setUp() {
        enrollmentCommandService = Mockito.mock(EnrollmentCommandService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new EnrollmentCommandController(enrollmentCommandService)).build();
        new ObjectMapper().findAndRegisterModules();
    }

    @Test
    void apply_callsServiceAndReturnsOk() throws Exception {
        when(enrollmentCommandService.apply(1L, 10L)).thenReturn(new ApplyEnrollmentResponse(
                100L,
                10L,
                1L,
                EnrollmentStatus.PENDING,
                LocalDateTime.of(2026, 5, 24, 10, 0),
                LocalDateTime.of(2026, 5, 25, 10, 0),
                null
        ));

        mockMvc.perform(post("/api/command/lectures/10/enrollments")
                        .header("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enrollmentId").value(100L))
                .andExpect(jsonPath("$.status").value("PENDING"));

        verify(enrollmentCommandService).apply(1L, 10L);
    }

    @Test
    void confirmPayment_callsServiceAndReturnsOk() throws Exception {
        when(enrollmentCommandService.confirmPayment(2L, 20L)).thenReturn(new ConfirmPaymentResponse(
                20L,
                EnrollmentStatus.CONFIRMED,
                LocalDateTime.of(2026, 5, 24, 12, 0)
        ));

        mockMvc.perform(post("/api/command/enrollments/20/confirm-payment")
                        .header("userId", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enrollmentId").value(20L))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));

        verify(enrollmentCommandService).confirmPayment(2L, 20L);
    }

    @Test
    void cancel_callsServiceAndReturnsOk() throws Exception {
        when(enrollmentCommandService.cancel(3L, 30L)).thenReturn(new CancelEnrollmentResponse(
                30L,
                EnrollmentStatus.CANCELLED,
                LocalDateTime.of(2026, 5, 24, 13, 0)
        ));

        mockMvc.perform(post("/api/command/enrollments/30/cancel")
                        .header("userId", 3L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enrollmentId").value(30L))
                .andExpect(jsonPath("$.status").value("CANCELLED"));

        verify(enrollmentCommandService).cancel(3L, 30L);
    }
}
