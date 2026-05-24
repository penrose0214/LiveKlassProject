INSERT INTO app_user (user_id, name)
VALUES (9301, 'creator-owner'),
       (9302, 'student-confirmed'),
       (9303, 'student-pending'),
       (9304, 'student-waitlisted');

INSERT INTO lecture (
    lecture_id,
    creator_id,
    title,
    description,
    price,
    capacity,
    recruitment_start_at,
    recruitment_end_at,
    lecture_start_at,
    lecture_end_at,
    status,
    created_at,
    updated_at
)
VALUES (
    10201,
    9301,
    '수강생 조회 강의',
    '크리에이터 전용 조회 검증',
    25000,
    5,
    CURRENT_TIMESTAMP - INTERVAL '1 day',
    CURRENT_TIMESTAMP + INTERVAL '1 day',
    CURRENT_TIMESTAMP + INTERVAL '7 day',
    CURRENT_TIMESTAMP + INTERVAL '14 day',
    'OPEN',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO enrollment (
    enrollment_id,
    lecture_id,
    user_id,
    status,
    applied_at,
    waitlisted_at,
    payment_due_at,
    confirmed_at,
    cancelled_at,
    created_at,
    updated_at
)
VALUES
    (
        20201,
        10201,
        9302,
        'CONFIRMED',
        CURRENT_TIMESTAMP - INTERVAL '2 day',
        NULL,
        CURRENT_TIMESTAMP - INTERVAL '1 day',
        CURRENT_TIMESTAMP - INTERVAL '1 day',
        NULL,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        20202,
        10201,
        9303,
        'PENDING',
        CURRENT_TIMESTAMP - INTERVAL '2 hour',
        NULL,
        CURRENT_TIMESTAMP + INTERVAL '12 hour',
        NULL,
        NULL,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        20203,
        10201,
        9304,
        'WAITLISTED',
        CURRENT_TIMESTAMP - INTERVAL '1 hour',
        CURRENT_TIMESTAMP - INTERVAL '1 hour',
        NULL,
        NULL,
        NULL,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    );
