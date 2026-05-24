package com.liveklass.command;

import com.liveklass.auth.AppUser;
import com.liveklass.command.domain.entity.Enrollment;
import com.liveklass.command.domain.entity.Lecture;
import com.liveklass.command.domain.enumeration.EnrollmentStatus;
import com.liveklass.command.domain.enumeration.LectureStatus;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StateTransitionSimulationBasedTest {

    @Test
    // LEC-STAT-001, LEC-STAT-002, LEC-STAT-003
    // 강의 상태 다이어그램의 모든 유효 edge인 DRAFT -> OPEN, OPEN -> CLOSED 전이를 순차적으로 검증한다.
    void lectureStateDiagram_allEdgesAreTraversable() {
        AppUser creator = new AppUser("creator");
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

        assertThat(lecture.getStatus()).isEqualTo(LectureStatus.DRAFT);

        lecture.changeStatus(LectureStatus.OPEN);
        assertThat(lecture.getStatus()).isEqualTo(LectureStatus.OPEN);

        lecture.changeStatus(LectureStatus.CLOSED);
        assertThat(lecture.getStatus()).isEqualTo(LectureStatus.CLOSED);
    }

    @Test
    // ENR-WAIT-002, ENR-PAY-002, ENR-CANCEL-001
    // 수강 신청 상태 다이어그램의 모든 유효 edge인 WAITLISTED -> PENDING, PENDING -> CONFIRMED,
    // WAITLISTED -> CANCELLED, PENDING -> CANCELLED, CONFIRMED -> CANCELLED 전이를 각각 검증한다.
    void enrollmentStateDiagram_allEdgesAreTraversable() {
        Lecture lecture = openLecture();
        AppUser student = new AppUser("student");
        LocalDateTime appliedAt = LocalDateTime.of(2026, 5, 24, 10, 0);

        Enrollment waitlistedForPromotion = Enrollment.waitlisted(lecture, student, appliedAt);
        assertThat(waitlistedForPromotion.getStatus()).isEqualTo(EnrollmentStatus.WAITLISTED);

        waitlistedForPromotion.promoteToPending(appliedAt.plusHours(12));
        assertThat(waitlistedForPromotion.getStatus()).isEqualTo(EnrollmentStatus.PENDING);

        waitlistedForPromotion.confirm(appliedAt.plusHours(13));
        assertThat(waitlistedForPromotion.getStatus()).isEqualTo(EnrollmentStatus.CONFIRMED);

        Enrollment waitlistedForCancel = Enrollment.waitlisted(lecture, student, appliedAt.plusMinutes(1));
        waitlistedForCancel.cancel(appliedAt.plusHours(1));
        assertThat(waitlistedForCancel.getStatus()).isEqualTo(EnrollmentStatus.CANCELLED);

        Enrollment pendingForCancel = Enrollment.pending(lecture, student, appliedAt.plusMinutes(2), appliedAt.plusHours(24));
        pendingForCancel.cancel(appliedAt.plusHours(2));
        assertThat(pendingForCancel.getStatus()).isEqualTo(EnrollmentStatus.CANCELLED);

        Enrollment confirmedForCancel = Enrollment.pending(lecture, student, appliedAt.plusMinutes(3), appliedAt.plusHours(24));
        confirmedForCancel.confirm(appliedAt.plusHours(3));
        confirmedForCancel.cancel(appliedAt.plusHours(4));
        assertThat(confirmedForCancel.getStatus()).isEqualTo(EnrollmentStatus.CANCELLED);
    }

    private Lecture openLecture() {
        AppUser creator = new AppUser("creator");
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
        lecture.changeStatus(LectureStatus.OPEN);
        return lecture;
    }
}
