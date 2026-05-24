package com.liveklass.command.domain.repository;

import com.liveklass.command.domain.entity.Enrollment;
import com.liveklass.command.domain.enumeration.EnrollmentStatus;
import java.util.Collection;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

public interface EnrollmentRepository extends Repository<Enrollment, Long> {

    Enrollment save(Enrollment enrollment);

    @Query("""
            select case when count(e) > 0 then true else false end
            from Enrollment e
            where e.lecture.id = :lectureId
              and e.user.id = :userId
              and e.status in :activeStatuses
            """)
    boolean existsActiveEnrollment(
            @Param("lectureId") Long lectureId,
            @Param("userId") Long userId,
            @Param("activeStatuses") Collection<EnrollmentStatus> activeStatuses
    );

    @Query("""
            select count(e)
            from Enrollment e
            where e.lecture.id = :lectureId
              and e.status in :occupiedStatuses
            """)
    long countOccupiedByLectureId(
            @Param("lectureId") Long lectureId,
            @Param("occupiedStatuses") Collection<EnrollmentStatus> occupiedStatuses
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from Enrollment e join fetch e.lecture join fetch e.user where e.id = :enrollmentId")
    Optional<Enrollment> findByIdForUpdate(@Param("enrollmentId") Long enrollmentId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select e
            from Enrollment e
            where e.lecture.id = :lectureId
              and e.status = :status
            order by e.waitlistedAt asc, e.id asc
            """)
    java.util.List<Enrollment> findFirstWaitlistedForUpdate(
            @Param("lectureId") Long lectureId,
            @Param("status") EnrollmentStatus status,
            Pageable pageable
    );
}
