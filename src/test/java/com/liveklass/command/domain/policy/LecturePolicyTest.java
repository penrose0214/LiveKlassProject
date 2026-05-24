package com.liveklass.command.domain.policy;

import com.liveklass.auth.AppUser;
import com.liveklass.command.domain.entity.Lecture;
import com.liveklass.command.domain.enumeration.LectureStatus;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LecturePolicyTest {

    private LecturePolicy lecturePolicy;

    @BeforeEach
    void setUp() {
        lecturePolicy = new LecturePolicy();
    }

    @Test
    // ACC-AUTHZ-001
    // 작성자가 아닌 사용자가 강의 요청을 수행하면 예외를 발생시키는지 검증한다.
    void validateCreator_whenNotCreator_throws() {
        Lecture lecture = lecture(1L, LectureStatus.DRAFT);

        assertThatThrownBy(() -> lecturePolicy.validateCreator(lecture, 2L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("강의 작성자");
    }

    @Test
    // LEC-STAT-003
    // DRAFT가 아닌 상태의 강의를 OPEN 하려 하면 예외를 발생시키는지 검증한다.
    void validateOpenable_whenNotDraft_throws() {
        Lecture lecture = lecture(1L, LectureStatus.OPEN);

        assertThatThrownBy(() -> lecturePolicy.validateOpenable(lecture))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("DRAFT");
    }

    private Lecture lecture(Long creatorId, LectureStatus status) {
        AppUser creator = new AppUser("creator");
        ReflectionTestUtils.setField(creator, "id", creatorId);
        Lecture lecture = Lecture.draft(
                creator,
                "title",
                "description",
                10000L,
                30,
                LocalDateTime.of(2026, 5, 24, 10, 0),
                LocalDateTime.of(2026, 5, 25, 10, 0),
                LocalDateTime.of(2026, 6, 1, 10, 0),
                LocalDateTime.of(2026, 6, 30, 10, 0)
        );
        lecture.changeStatus(status);
        return lecture;
    }
}
