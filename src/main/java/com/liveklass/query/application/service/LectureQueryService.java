package com.liveklass.query.application.service;

import com.liveklass.command.domain.enumeration.LectureStatus;
import com.liveklass.query.application.dto.LectureDetailResponse;
import com.liveklass.query.application.dto.LectureSummaryResponse;
import com.liveklass.query.domain.repository.LectureQueryRepository;
import java.util.EnumSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LectureQueryService {

    private final LectureQueryRepository lectureQueryRepository;

    public Page<LectureSummaryResponse> getLectures(List<LectureStatus> statuses, int page, int size) {
        List<LectureStatus> effectiveStatuses = (statuses == null || statuses.isEmpty())
                ? List.copyOf(EnumSet.allOf(LectureStatus.class))
                : statuses;

        return lectureQueryRepository.findLectureSummariesByStatuses(effectiveStatuses, PageRequest.of(page, size));
    }

    public LectureDetailResponse getLectureDetail(Long lectureId) {
        return lectureQueryRepository.findLectureDetail(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("강의를 찾을 수 없습니다. id=" + lectureId));
    }
}
