package com.liveklass.command.domain.policy;

import com.liveklass.command.domain.entity.Lecture;
import com.liveklass.command.domain.enumeration.LectureStatus;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class LecturePolicy {

    public void validateCreator(Lecture lecture, Long userId) {
        if (!lecture.getCreator().getId().equals(userId)) {
            throw new IllegalArgumentException("강의 작성자만 요청할 수 있습니다.");
        }
    }

    public void validateOpenable(Lecture lecture) {
        if (lecture.getStatus() != LectureStatus.DRAFT) {
            throw new IllegalArgumentException("DRAFT 상태의 강의만 OPEN 할 수 있습니다.");
        }
    }

    public void validateClosable(Lecture lecture) {
        if (lecture.getStatus() == LectureStatus.CLOSED) {
            throw new IllegalArgumentException("이미 CLOSED 상태인 강의입니다.");
        }
    }

    public void validateRecruitmentOpen(Lecture lecture, LocalDateTime now) {
        if (lecture.getStatus() != LectureStatus.OPEN) {
            throw new IllegalArgumentException("OPEN 상태의 강의만 신청할 수 있습니다.");
        }
        if (now.isBefore(lecture.getRecruitmentStartAt()) || now.isAfter(lecture.getRecruitmentEndAt())) {
            throw new IllegalArgumentException("모집 기간이 아닙니다.");
        }
    }
}
