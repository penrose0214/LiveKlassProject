package com.liveklass.integration;

import com.liveklass.command.application.dto.ApplyEnrollmentResponse;
import com.liveklass.command.application.service.EnrollmentCommandService;
import com.liveklass.command.domain.entity.Enrollment;
import com.liveklass.command.domain.enumeration.EnrollmentStatus;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "/sql/command/enrollment_setup.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/command/enrollment_cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
class EnrollmentCommandIntegratedTest {

    @Autowired
    private EnrollmentCommandService enrollmentCommandService;

    @Autowired
    private EntityManager entityManager;

    @Test
    // ENR-APPLY-001, ENR-PAY-001
    // 실제 DB 기반으로 수강 신청 시 PENDING 생성과 paymentDueAt 계산이 함께 반영되는지 검증한다.
    void apply_persistsPendingEnrollmentWithDueAt() {
        ApplyEnrollmentResponse response = enrollmentCommandService.apply(9402L, 10301L);

        entityManager.flush();
        entityManager.clear();

        Enrollment enrollment = entityManager.find(Enrollment.class, response.enrollmentId());

        assertThat(response.status()).isEqualTo(EnrollmentStatus.PENDING);
        assertThat(response.paymentDueAt()).isNotNull();
        assertThat(enrollment).isNotNull();
        assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.PENDING);
        assertThat(enrollment.getPaymentDueAt()).isEqualTo(response.paymentDueAt());
        assertThat(enrollment.getPaymentDueAt()).isBeforeOrEqualTo(enrollment.getLecture().getRecruitmentEndAt());
        assertThat(enrollment.getPaymentDueAt()).isAfter(LocalDateTime.now().minusMinutes(1));
    }
}
