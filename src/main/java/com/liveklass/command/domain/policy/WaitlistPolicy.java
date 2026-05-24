package com.liveklass.command.domain.policy;

import com.liveklass.command.domain.entity.Enrollment;
import com.liveklass.command.domain.entity.Lecture;
import com.liveklass.command.domain.enumeration.LectureStatus;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class WaitlistPolicy {

    public boolean canPromote(Lecture lecture, LocalDateTime now) {
        return lecture.getStatus() == LectureStatus.OPEN
                && !now.isBefore(lecture.getRecruitmentStartAt())
                && !now.isAfter(lecture.getRecruitmentEndAt());
    }

    public void validateWaitlisted(Enrollment enrollment) {
        if (enrollment.getWaitlistedAt() == null) {
            throw new IllegalArgumentException("대기열 신청 정보가 올바르지 않습니다.");
        }
    }
}
