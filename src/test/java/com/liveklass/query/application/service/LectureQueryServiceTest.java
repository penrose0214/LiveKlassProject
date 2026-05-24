package com.liveklass.query.application.service;

import com.liveklass.command.domain.enumeration.LectureStatus;
import com.liveklass.query.application.dto.LectureDetailResponse;
import com.liveklass.query.application.dto.LectureSummaryResponse;
import com.liveklass.query.domain.repository.LectureQueryRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LectureQueryServiceTest {

    @Mock
    private LectureQueryRepository lectureQueryRepository;

    private LectureQueryService lectureQueryService;

    @BeforeEach
    void setUp() {
        lectureQueryService = new LectureQueryService(lectureQueryRepository);
    }

    @Test
    void getLectures_returnsRepositoryResult() {
        List<LectureSummaryResponse> responses = List.of(new LectureSummaryResponse(
                1L,
                2L,
                "creator",
                "title",
                10000L,
                30,
                LocalDateTime.of(2026, 5, 24, 10, 0),
                LocalDateTime.of(2026, 5, 25, 10, 0),
                LocalDateTime.of(2026, 6, 1, 10, 0),
                LocalDateTime.of(2026, 6, 30, 10, 0),
                LectureStatus.OPEN
        ));
        when(lectureQueryRepository.findLectureSummaries()).thenReturn(responses);

        List<LectureSummaryResponse> result = lectureQueryService.getLectures();

        assertThat(result).isEqualTo(responses);
        verify(lectureQueryRepository).findLectureSummaries();
    }

    @Test
    void getLectureDetail_whenFound_returnsDetail() {
        LectureDetailResponse response = new LectureDetailResponse(
                10L,
                2L,
                "creator",
                "title",
                "description",
                10000L,
                30,
                LocalDateTime.of(2026, 5, 24, 10, 0),
                LocalDateTime.of(2026, 5, 25, 10, 0),
                LocalDateTime.of(2026, 6, 1, 10, 0),
                LocalDateTime.of(2026, 6, 30, 10, 0),
                LectureStatus.OPEN,
                3L,
                2L,
                1L
        );
        when(lectureQueryRepository.findLectureDetail(10L)).thenReturn(Optional.of(response));

        LectureDetailResponse result = lectureQueryService.getLectureDetail(10L);

        assertThat(result).isEqualTo(response);
    }

    @Test
    void getLectureDetail_whenMissing_throws() {
        when(lectureQueryRepository.findLectureDetail(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> lectureQueryService.getLectureDetail(10L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("강의를 찾을 수 없습니다");
    }
}
