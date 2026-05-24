package com.liveklass.query.application.service;

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

    public Page<MyEnrollmentResponse> getMyEnrollments(Long userId, int page, int size) {
        return enrollmentQueryRepository.findMyEnrollments(userId, PageRequest.of(page, size));
    }

    public List<LectureStudentResponse> getLectureStudents(Long lectureId) {
        return enrollmentQueryRepository.findConfirmedStudentsByLecture(lectureId);
    }
}
