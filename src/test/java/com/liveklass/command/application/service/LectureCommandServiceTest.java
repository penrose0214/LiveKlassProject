package com.liveklass.command.application.service;

import com.liveklass.auth.AppUser;
import com.liveklass.command.application.dto.CreateLectureRequest;
import com.liveklass.command.application.dto.UpdateLectureRequest;
import com.liveklass.command.domain.entity.Lecture;
import com.liveklass.command.domain.enumeration.LectureStatus;
import com.liveklass.command.domain.policy.LecturePolicy;
import com.liveklass.command.domain.repository.LectureRepository;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LectureCommandServiceTest {

    @Mock
    private LectureRepository lectureRepository;
    @Mock
    private LecturePolicy lecturePolicy;
    @Mock
    private EntityManager entityManager;

    private LectureCommandService lectureCommandService;

    @BeforeEach
    void setUp() {
        lectureCommandService = new LectureCommandService(lectureRepository, lecturePolicy, entityManager);
    }

    @Test
    void createLecture_savesDraftLecture() {
        AppUser creator = new AppUser("creator");
        ReflectionTestUtils.setField(creator, "id", 1L);
        CreateLectureRequest request = new CreateLectureRequest(
                "title",
                "description",
                10000L,
                30,
                LocalDateTime.of(2026, 5, 24, 10, 0),
                LocalDateTime.of(2026, 5, 25, 10, 0),
                LocalDateTime.of(2026, 6, 1, 10, 0),
                LocalDateTime.of(2026, 6, 30, 10, 0)
        );
        when(entityManager.getReference(AppUser.class, 1L)).thenReturn(creator);

        lectureCommandService.createLecture(1L, request);

        ArgumentCaptor<Lecture> captor = ArgumentCaptor.forClass(Lecture.class);
        verify(lectureRepository).save(captor.capture());
        Lecture saved = captor.getValue();
        assertThat(saved.getCreator().getId()).isEqualTo(1L);
        assertThat(saved.getTitle()).isEqualTo("title");
        assertThat(saved.getStatus()).isEqualTo(LectureStatus.DRAFT);
    }

    @Test
    void updateLecture_updatesLectureFields() {
        Lecture lecture = lectureWithStatus(LectureStatus.DRAFT);
        UpdateLectureRequest request = new UpdateLectureRequest(
                "new-title",
                "new-description",
                20000L,
                20,
                LocalDateTime.of(2026, 5, 24, 11, 0),
                LocalDateTime.of(2026, 5, 25, 11, 0),
                LocalDateTime.of(2026, 6, 2, 11, 0),
                LocalDateTime.of(2026, 7, 1, 11, 0)
        );
        when(lectureRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(lecture));

        lectureCommandService.updateLecture(1L, 10L, request);

        verify(lecturePolicy).validateCreator(lecture, 1L);
        assertThat(lecture.getTitle()).isEqualTo("new-title");
        assertThat(lecture.getPrice()).isEqualTo(20000L);
    }

    @Test
    void openLecture_changesStatusToOpen() {
        Lecture lecture = lectureWithStatus(LectureStatus.DRAFT);
        when(lectureRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(lecture));

        lectureCommandService.openLecture(1L, 10L);

        verify(lecturePolicy).validateCreator(lecture, 1L);
        verify(lecturePolicy).validateOpenable(lecture);
        assertThat(lecture.getStatus()).isEqualTo(LectureStatus.OPEN);
    }

    @Test
    void closeLecture_changesStatusToClosed() {
        Lecture lecture = lectureWithStatus(LectureStatus.OPEN);
        when(lectureRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(lecture));

        lectureCommandService.closeLecture(1L, 10L);

        verify(lecturePolicy).validateCreator(lecture, 1L);
        verify(lecturePolicy).validateClosable(lecture);
        assertThat(lecture.getStatus()).isEqualTo(LectureStatus.CLOSED);
    }

    private Lecture lectureWithStatus(LectureStatus status) {
        AppUser creator = new AppUser("creator");
        ReflectionTestUtils.setField(creator, "id", 1L);
        Lecture lecture = Lecture.draft(
                creator,
                "title",
                "description",
                10000L,
                30,
                LocalDateTime.of(2026, 5, 24, 10, 0),
                LocalDateTime.of(2026, 5, 25, 10, 0),
                LocalDateTime.of(2026, 6, 1, 10, 0),
                LocalDateTime.of(2026, 6, 30, 10, 0)
        );
        ReflectionTestUtils.setField(lecture, "id", 10L);
        lecture.changeStatus(status);
        return lecture;
    }
}
