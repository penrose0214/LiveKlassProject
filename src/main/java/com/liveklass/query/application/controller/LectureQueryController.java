package com.liveklass.query.application.controller;

import com.liveklass.query.application.dto.LectureDetailResponse;
import com.liveklass.query.application.dto.LectureSummaryResponse;
import com.liveklass.query.application.service.LectureQueryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/query/lectures")
@RequiredArgsConstructor
public class LectureQueryController {

    private final LectureQueryService lectureQueryService;

    // LEC-LIST-001, 002
    // 강의 목록 조회
    @GetMapping
    public ResponseEntity<List<LectureSummaryResponse>> getLectures() {
        return ResponseEntity.ok(lectureQueryService.getLectures());
    }

    // LEC-DETAIL-001
    // 강의 상세 조회
    @GetMapping("/{lectureId}")
    public ResponseEntity<LectureDetailResponse> getLectureDetail(@PathVariable Long lectureId) {
        return ResponseEntity.ok(lectureQueryService.getLectureDetail(lectureId));
    }
}
