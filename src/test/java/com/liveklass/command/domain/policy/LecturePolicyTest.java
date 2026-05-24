package com.liveklass.command.domain.policy;

import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LecturePolicyTest {

    private LecturePolicy lecturePolicy;

    @BeforeEach
    void setUp() {
        lecturePolicy = new LecturePolicy();
    }

    @Test
    // LEC-REG-002
    // 제목, 설명, 가격, 정원, 일정 순서가 유효하면 예외가 발생하지 않는지 검증한다.
    void validateLectureDetails_whenValid_doesNotThrow() {
        assertThatCode(() -> lecturePolicy.validateLectureDetails(
                "title",
                "description",
                10000L,
                30,
                LocalDateTime.of(2026, 5, 24, 10, 0),
                LocalDateTime.of(2026, 5, 25, 10, 0),
                LocalDateTime.of(2026, 5, 25, 10, 0),
                LocalDateTime.of(2026, 6, 30, 10, 0)
        )).doesNotThrowAnyException();
    }

    @Test
    // LEC-REG-002
    // 모집 마감일이 수강 시작일보다 늦으면 예외를 발생시키는지 검증한다.
    void validateLectureDetails_whenRecruitmentEndAfterLectureStart_throws() {
        assertThatThrownBy(() -> lecturePolicy.validateLectureDetails(
                "title",
                "description",
                10000L,
                30,
                LocalDateTime.of(2026, 5, 24, 10, 0),
                LocalDateTime.of(2026, 6, 2, 10, 1),
                LocalDateTime.of(2026, 6, 2, 10, 0),
                LocalDateTime.of(2026, 6, 30, 10, 0)
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("모집 마감일");
    }
}
