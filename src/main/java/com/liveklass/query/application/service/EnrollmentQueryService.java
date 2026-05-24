package com.liveklass.query.application.service;

import com.liveklass.query.application.dto.LectureStudentResponse;
import com.liveklass.query.application.dto.MyEnrollmentResponse;
import com.liveklass.query.domain.repository.EnrollmentQueryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EnrollmentQueryService {

    private final EnrollmentQueryRepository enrollmentQueryRepository;

    public List<MyEnrollmentResponse> getMyEnrollments(Long userId) {
        return enrollmentQueryRepository.findMyEnrollments(userId);
    }

    public List<LectureStudentResponse> getLectureStudents(Long lectureId) {
        return enrollmentQueryRepository.findConfirmedStudentsByLecture(lectureId);
    }
}
