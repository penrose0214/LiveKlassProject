package com.liveklass.command;

import com.liveklass.auth.AppUser;
import com.liveklass.command.application.service.EnrollmentCommandService;
import com.liveklass.command.domain.entity.Lecture;
import com.liveklass.command.domain.enumeration.EnrollmentStatus;
import com.liveklass.command.domain.enumeration.LectureStatus;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EnrollmentConcurrencySimulationBasedTest {

    @Autowired
    private EnrollmentCommandService enrollmentCommandService;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private PlatformTransactionManager transactionManager;

    private TransactionTemplate transactionTemplate;

    @BeforeEach
    void setUp() {
        transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @AfterEach
    void tearDown() {
        transactionTemplate.executeWithoutResult(status -> {
            entityManager.createQuery("delete from Enrollment").executeUpdate();
            entityManager.createQuery("delete from Lecture").executeUpdate();
            entityManager.createQuery("delete from AppUser").executeUpdate();
        });
    }

    @Test
    // ENR-APPLY-001, ENR-WAIT-001, CAP-RULE-001, CAP-RULE-002, CAP-CONC-001, CAP-CONC-002
    // 정원을 초과하는 인원이 동시에 수강 신청하면 정원 수만큼은 PENDING으로,
    // 초과 인원은 WAITLISTED로 쌓이는지 실제 트랜잭션과 락 기반으로 검증한다.
    void applyConcurrently_whenCapacityExceeded_waitlistedStudentsAccumulate() throws Exception {
        int capacity = 3;
        int applicantCount = 10;

        Long lectureId = createOpenLecture(capacity);
        List<Long> applicantIds = createApplicants(applicantCount);

        ExecutorService executorService = Executors.newFixedThreadPool(applicantCount);
        CountDownLatch ready = new CountDownLatch(applicantCount);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(applicantCount);
        Queue<Throwable> failures = new ConcurrentLinkedQueue<>();

        for (Long applicantId : applicantIds) {
            executorService.submit(() -> {
                ready.countDown();
                try {
                    start.await();
                    enrollmentCommandService.apply(applicantId, lectureId);
                } catch (Throwable throwable) {
                    failures.add(throwable);
                } finally {
                    done.countDown();
                }
            });
        }

        assertThat(ready.await(5, TimeUnit.SECONDS)).isTrue();
        start.countDown();
        assertThat(done.await(10, TimeUnit.SECONDS)).isTrue();

        executorService.shutdown();
        assertThat(executorService.awaitTermination(5, TimeUnit.SECONDS)).isTrue();
        assertThat(failures).isEmpty();

        Map<EnrollmentStatus, Long> countsByStatus = fetchEnrollmentCounts(lectureId);

        assertThat(countsByStatus.getOrDefault(EnrollmentStatus.PENDING, 0L)).isEqualTo(capacity);
        assertThat(countsByStatus.getOrDefault(EnrollmentStatus.WAITLISTED, 0L)).isEqualTo(applicantCount - capacity);
        assertThat(countsByStatus.getOrDefault(EnrollmentStatus.CONFIRMED, 0L)).isZero();
        assertThat(countsByStatus.getOrDefault(EnrollmentStatus.CANCELLED, 0L)).isZero();
    }

    private Long createOpenLecture(int capacity) {
        return transactionTemplate.execute(status -> {
            AppUser creator = new AppUser("creator");
            entityManager.persist(creator);

            Lecture lecture = Lecture.draft(
                    creator,
                    "동시성 테스트 강의",
                    "정원 초과 대기열 검증",
                    10000L,
                    capacity,
                    LocalDateTime.now().minusDays(1),
                    LocalDateTime.now().plusDays(1),
                    LocalDateTime.now().plusDays(10),
                    LocalDateTime.now().plusDays(20)
            );
            lecture.changeStatus(LectureStatus.OPEN);
            entityManager.persist(lecture);
            entityManager.flush();
            return lecture.getId();
        });
    }

    private List<Long> createApplicants(int applicantCount) {
        return transactionTemplate.execute(status -> {
            List<Long> ids = new ArrayList<>();
            IntStream.rangeClosed(1, applicantCount).forEach(i -> {
                AppUser applicant = new AppUser("student-" + i);
                entityManager.persist(applicant);
                entityManager.flush();
                ids.add(applicant.getId());
            });
            return ids;
        });
    }

    private Map<EnrollmentStatus, Long> fetchEnrollmentCounts(Long lectureId) {
        return transactionTemplate.execute(status -> {
            entityManager.clear();
            List<Object[]> rows = entityManager.createQuery(
                            "select e.status, count(e) from Enrollment e where e.lecture.id = :lectureId group by e.status",
                            Object[].class
                    )
                    .setParameter("lectureId", lectureId)
                    .getResultList();

            Map<EnrollmentStatus, Long> result = new EnumMap<>(EnrollmentStatus.class);
            for (Object[] row : rows) {
                result.put((EnrollmentStatus) row[0], (Long) row[1]);
            }
            return result;
        });
    }
}
