package com.liveklass.query.domain.repository;

import com.liveklass.command.domain.entity.Enrollment;
import com.liveklass.command.domain.enumeration.EnrollmentStatus;
import com.liveklass.query.application.dto.LectureStudentResponse;
import com.liveklass.query.application.dto.MyEnrollmentResponse;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    Page<MyEnrollmentResponse> findMyEnrollments(@Param("userId") Long userId, Pageable pageable);

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
              and e.status in :statuses
            order by
                case when e.confirmedAt is null then 1 else 0 end,
                e.confirmedAt asc,
                e.appliedAt asc,
                e.id asc
            """)
    List<LectureStudentResponse> findStudentsByLectureAndStatuses(
            @Param("lectureId") Long lectureId,
            @Param("statuses") Collection<EnrollmentStatus> statuses
    );
}
