package com.liveklass.query.domain.repository;

import com.liveklass.command.domain.entity.Lecture;
import com.liveklass.query.application.dto.LectureDetailResponse;
import com.liveklass.query.application.dto.LectureSummaryResponse;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface LectureQueryRepository extends Repository<Lecture, Long> {

    @Query("""
            select new com.liveklass.query.application.dto.LectureSummaryResponse(
                l.id,
                l.creator.id,
                l.creator.name,
                l.title,
                l.price,
                l.capacity,
                l.recruitmentStartAt,
                l.recruitmentEndAt,
                l.lectureStartAt,
                l.lectureEndAt,
                l.status
            )
            from Lecture l
            order by l.id desc
            """)
    Page<LectureSummaryResponse> findLectureSummaries(Pageable pageable);

    @Query("""
            select new com.liveklass.query.application.dto.LectureDetailResponse(
                l.id,
                l.creator.id,
                l.creator.name,
                l.title,
                l.description,
                l.price,
                l.capacity,
                l.recruitmentStartAt,
                l.recruitmentEndAt,
                l.lectureStartAt,
                l.lectureEndAt,
                l.status,
                (select count(e1) from Enrollment e1
                    where e1.lecture.id = l.id
                      and e1.status in (com.liveklass.command.domain.enumeration.EnrollmentStatus.PENDING,
                                        com.liveklass.command.domain.enumeration.EnrollmentStatus.CONFIRMED)),
                (select count(e2) from Enrollment e2
                    where e2.lecture.id = l.id
                      and e2.status = com.liveklass.command.domain.enumeration.EnrollmentStatus.CONFIRMED),
                (select count(e3) from Enrollment e3
                    where e3.lecture.id = l.id
                      and e3.status = com.liveklass.command.domain.enumeration.EnrollmentStatus.WAITLISTED)
            )
            from Lecture l
            where l.id = :lectureId
            """)
    Optional<LectureDetailResponse> findLectureDetail(@Param("lectureId") Long lectureId);
}
