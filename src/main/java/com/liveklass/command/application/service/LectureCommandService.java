package com.liveklass.command.application.service;

import com.liveklass.auth.AppUser;
import com.liveklass.command.application.dto.CreateLectureRequest;
import com.liveklass.command.application.dto.UpdateLectureRequest;
import com.liveklass.command.domain.entity.Lecture;
import com.liveklass.command.domain.enumeration.LectureStatus;
import com.liveklass.command.domain.policy.LecturePolicy;
import com.liveklass.command.domain.repository.LectureRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LectureCommandService {

    private final LectureRepository lectureRepository;
    private final LecturePolicy lecturePolicy;
    private final EntityManager entityManager;

    public void createLecture(Long userId, CreateLectureRequest request) {
        AppUser creator = entityManager.getReference(AppUser.class, userId);
        Lecture lecture = Lecture.draft(
                creator,
                request.title(),
                request.description(),
                request.price(),
                request.capacity(),
                request.recruitmentStartAt(),
                request.recruitmentEndAt(),
                request.lectureStartAt(),
                request.lectureEndAt()
        );
        lectureRepository.save(lecture);
    }

    public void updateLecture(Long userId, Long lectureId, UpdateLectureRequest request) {
        Lecture lecture = lectureRepository.findByIdForUpdate(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("강의를 찾을 수 없습니다. id=" + lectureId));
        lecturePolicy.validateCreator(lecture, userId);
        if (lecture.getStatus() == LectureStatus.CLOSED) {
            throw new IllegalArgumentException("CLOSED 상태 강의는 수정할 수 없습니다.");
        }
        lecture.updateDetails(
                request.title(),
                request.description(),
                request.price(),
                request.capacity(),
                request.recruitmentStartAt(),
                request.recruitmentEndAt(),
                request.lectureStartAt(),
                request.lectureEndAt()
        );
    }

    public void openLecture(Long userId, Long lectureId) {
        Lecture lecture = lectureRepository.findByIdForUpdate(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("강의를 찾을 수 없습니다. id=" + lectureId));
        lecturePolicy.validateCreator(lecture, userId);
        lecturePolicy.validateOpenable(lecture);
        lecture.changeStatus(LectureStatus.OPEN);
    }

    public void closeLecture(Long userId, Long lectureId) {
        Lecture lecture = lectureRepository.findByIdForUpdate(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("강의를 찾을 수 없습니다. id=" + lectureId));
        lecturePolicy.validateCreator(lecture, userId);
        lecturePolicy.validateClosable(lecture);
        lecture.changeStatus(LectureStatus.CLOSED);
    }
}
