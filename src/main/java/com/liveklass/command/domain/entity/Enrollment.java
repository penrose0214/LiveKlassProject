package com.liveklass.command.domain.entity;

import com.liveklass.auth.AppUser;
import com.liveklass.command.domain.enumeration.EnrollmentStatus;
import com.liveklass.common.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "enrollment"
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Enrollment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "enrollment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lecture_id", nullable = false)
    private Lecture lecture;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private EnrollmentStatus status;

    @Column(name = "applied_at", nullable = false)
    private LocalDateTime appliedAt;

    @Column(name = "waitlisted_at")
    private LocalDateTime waitlistedAt;

    @Column(name = "payment_due_at")
    private LocalDateTime paymentDueAt;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "active_user_id", insertable = false, updatable = false)
    private Long activeUserId;

    public Enrollment(
            Lecture lecture,
            AppUser user,
            EnrollmentStatus status,
            LocalDateTime appliedAt,
            LocalDateTime waitlistedAt,
            LocalDateTime paymentDueAt,
            LocalDateTime confirmedAt,
            LocalDateTime cancelledAt
    ) {
        this.lecture = lecture;
        this.user = user;
        this.status = status;
        this.appliedAt = appliedAt;
        this.waitlistedAt = waitlistedAt;
        this.paymentDueAt = paymentDueAt;
        this.confirmedAt = confirmedAt;
        this.cancelledAt = cancelledAt;
    }

    public static Enrollment pending(
            Lecture lecture,
            AppUser user,
            LocalDateTime appliedAt,
            LocalDateTime paymentDueAt
    ) {
        return new Enrollment(
                lecture,
                user,
                EnrollmentStatus.PENDING,
                appliedAt,
                null,
                paymentDueAt,
                null,
                null
        );
    }

    public static Enrollment waitlisted(
            Lecture lecture,
            AppUser user,
            LocalDateTime appliedAt
    ) {
        return new Enrollment(
                lecture,
                user,
                EnrollmentStatus.WAITLISTED,
                appliedAt,
                appliedAt,
                null,
                null,
                null
        );
    }
}
