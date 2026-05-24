package com.liveklass.query.application.controller;

import com.liveklass.query.application.dto.LectureStudentResponse;
import com.liveklass.query.application.dto.MyEnrollmentResponse;
import com.liveklass.query.application.service.EnrollmentQueryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/query")
@RequiredArgsConstructor
public class EnrollmentQueryController {

    private final EnrollmentQueryService enrollmentQueryService;

    // ENR-LIST-001
    // 수강 신청 내역 조회
    @GetMapping("/enrollments/me")
    public ResponseEntity<Page<MyEnrollmentResponse>> getMyEnrollments(
            @RequestHeader("userId") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(enrollmentQueryService.getMyEnrollments(userId, page, size));
    }

    //
    @GetMapping("/lectures/{lectureId}/students")
    public ResponseEntity<List<LectureStudentResponse>> getLectureStudents(@PathVariable Long lectureId) {
        return ResponseEntity.ok(enrollmentQueryService.getLectureStudents(lectureId));
    }
}
