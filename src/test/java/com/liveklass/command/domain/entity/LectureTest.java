package com.liveklass.command.domain.entity;

import com.liveklass.auth.AppUser;
import com.liveklass.common.exception.DomainValidationException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LectureTest {

    @Test
    // LEC-REG-002
    // 공백 제목으로 강의를 생성하면 엔티티에서 예외를 발생시키는지 검증한다.
    void draft_whenTitleIsBlank_throws() {
        assertThatThrownBy(() -> Lecture.draft(
                new AppUser("creator"),
                " ",
                "description",
                10000L,
                30,
                LocalDateTime.of(2026, 5, 24, 10, 0),
                LocalDateTime.of(2026, 5, 25, 10, 0),
                LocalDateTime.of(2026, 6, 1, 10, 0),
                LocalDateTime.of(2026, 6, 30, 10, 0)
        )).isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("강의 제목");
    }

    @Test
    // LEC-REG-002
    // 모집 마감일이 수강 시작일보다 늦으면 엔티티에서 예외를 발생시키는지 검증한다.
    void draft_whenRecruitmentEndAfterLectureStart_throws() {
        assertThatThrownBy(() -> Lecture.draft(
                new AppUser("creator"),
                "title",
                "description",
                10000L,
                30,
                LocalDateTime.of(2026, 5, 24, 10, 0),
                LocalDateTime.of(2026, 6, 1, 10, 1),
                LocalDateTime.of(2026, 6, 1, 10, 0),
                LocalDateTime.of(2026, 6, 30, 10, 0)
        )).isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("모집 마감일");
    }
}
