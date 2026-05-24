package com.liveklass.query.application.service;

import com.liveklass.query.application.dto.LectureDetailResponse;
import com.liveklass.query.application.dto.LectureSummaryResponse;
import com.liveklass.query.domain.repository.LectureQueryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LectureQueryService {

    private final LectureQueryRepository lectureQueryRepository;

    public List<LectureSummaryResponse> getLectures() {
        return lectureQueryRepository.findLectureSummaries();
    }

    public LectureDetailResponse getLectureDetail(Long lectureId) {
        return lectureQueryRepository.findLectureDetail(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("강의를 찾을 수 없습니다. id=" + lectureId));
    }
}
