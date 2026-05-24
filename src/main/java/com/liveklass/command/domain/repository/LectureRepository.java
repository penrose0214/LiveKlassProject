package com.liveklass.command.domain.repository;

import com.liveklass.command.domain.entity.Lecture;
import java.util.Optional;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

public interface LectureRepository extends Repository<Lecture, Long> {

    Lecture save(Lecture lecture);

    Optional<Lecture> findById(Long lectureId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select l from Lecture l where l.id = :lectureId")
    Optional<Lecture> findByIdForUpdate(@Param("lectureId") Long lectureId);
}
