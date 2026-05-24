package com.liveklass.command.domain.policy;

import com.liveklass.command.domain.entity.Lecture;
import com.liveklass.command.domain.enumeration.LectureStatus;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class LecturePolicy {

    public void validateLectureDetails(
            String title,
            String description,
            Long price,
            Integer capacity,
            LocalDateTime recruitmentStartAt,
            LocalDateTime recruitmentEndAt,
            LocalDateTime lectureStartAt,
            LocalDateTime lectureEndAt
    ) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("강의 제목은 필수이며 공백일 수 없습니다.");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("강의 설명은 필수이며 공백일 수 없습니다.");
        }
        if (price == null || price < 0) {
            throw new IllegalArgumentException("강의 가격은 0 이상이어야 합니다.");
        }
        if (capacity == null || capacity < 1) {
            throw new IllegalArgumentException("강의 정원은 1 이상이어야 합니다.");
        }
        if (recruitmentStartAt == null || recruitmentEndAt == null || lectureStartAt == null || lectureEndAt == null) {
            throw new IllegalArgumentException("강의 일정은 모두 입력되어야 합니다.");
        }
        if (recruitmentStartAt.isAfter(recruitmentEndAt)) {
            throw new IllegalArgumentException("모집 시작일은 모집 마감일보다 이전이거나 같아야 합니다.");
        }
        if (lectureStartAt.isAfter(lectureEndAt)) {
            throw new IllegalArgumentException("수강 시작일은 수강 종료일보다 이전이거나 같아야 합니다.");
        }
        if (recruitmentEndAt.isAfter(lectureStartAt)) {
            throw new IllegalArgumentException("모집 마감일은 수강 시작일보다 이전이거나 같아야 합니다.");
        }
    }

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
