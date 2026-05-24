package com.liveklass.query.application.service;

import com.liveklass.command.domain.entity.Lecture;
import com.liveklass.command.domain.enumeration.EnrollmentStatus;
import com.liveklass.command.domain.policy.LecturePolicy;
import com.liveklass.command.domain.repository.LectureRepository;
import com.liveklass.query.application.dto.LectureStudentResponse;
import com.liveklass.query.application.dto.MyEnrollmentResponse;
import com.liveklass.query.domain.repository.EnrollmentQueryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EnrollmentQueryService {

    private final EnrollmentQueryRepository enrollmentQueryRepository;
    private final LectureRepository lectureRepository;
    private final LecturePolicy lecturePolicy;

    public Page<MyEnrollmentResponse> getMyEnrollments(Long userId, int page, int size) {
        return enrollmentQueryRepository.findMyEnrollments(userId, PageRequest.of(page, size));
    }

    public List<LectureStudentResponse> getLectureStudents(
            Long userId,
            Long lectureId,
            List<EnrollmentStatus> statuses
    ) {
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("강의를 찾을 수 없습니다. id=" + lectureId));
        lecturePolicy.validateCreator(lecture, userId);

        List<EnrollmentStatus> effectiveStatuses = (statuses == null || statuses.isEmpty())
                ? List.of(EnrollmentStatus.CONFIRMED)
                : statuses;

        return enrollmentQueryRepository.findStudentsByLectureAndStatuses(lectureId, effectiveStatuses);
    }
}
