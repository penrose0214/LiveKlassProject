package com.liveklass.query.domain.repository;

import com.liveklass.command.domain.entity.Enrollment;
import com.liveklass.query.application.dto.LectureStudentResponse;
import com.liveklass.query.application.dto.MyEnrollmentResponse;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface EnrollmentQueryRepository extends Repository<Enrollment, Long> {

    @Query("""
            select new com.liveklass.query.application.dto.MyEnrollmentResponse(
                e.id,
                l.id,
                l.title,
                l.creator.name,
                l.status,
                e.status,
                l.price,
                e.appliedAt,
                e.paymentDueAt,
                e.confirmedAt,
                e.cancelledAt,
                l.lectureStartAt,
                l.lectureEndAt
            )
            from Enrollment e
            join e.lecture l
            where e.user.id = :userId
            order by e.appliedAt desc, e.id desc
            """)
    List<MyEnrollmentResponse> findMyEnrollments(@Param("userId") Long userId);

    @Query("""
            select new com.liveklass.query.application.dto.LectureStudentResponse(
                e.id,
                e.user.id,
                e.user.name,
                e.status,
                e.appliedAt,
                e.confirmedAt
            )
            from Enrollment e
            where e.lecture.id = :lectureId
              and e.status = com.liveklass.command.domain.enumeration.EnrollmentStatus.CONFIRMED
            order by e.confirmedAt asc, e.id asc
            """)
    List<LectureStudentResponse> findConfirmedStudentsByLecture(@Param("lectureId") Long lectureId);
}
