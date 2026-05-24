INSERT INTO app_user (user_id, name)
VALUES (9101, 'creator-a'),
       (9102, 'creator-b'),
       (9201, 'student-a'),
       (9202, 'student-b'),
       (9203, 'student-c');

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
VALUES
    (
        10101,
        9101,
        'OPEN 강의',
        '모집 중 강의',
        30000,
        3,
        CURRENT_TIMESTAMP - INTERVAL '1 day',
        CURRENT_TIMESTAMP + INTERVAL '1 day',
        CURRENT_TIMESTAMP + INTERVAL '7 day',
        CURRENT_TIMESTAMP + INTERVAL '14 day',
        'OPEN',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        10102,
        9102,
        'CLOSED 강의',
        '마감 강의',
        40000,
        2,
        CURRENT_TIMESTAMP - INTERVAL '10 day',
        CURRENT_TIMESTAMP - INTERVAL '5 day',
        CURRENT_TIMESTAMP + INTERVAL '3 day',
        CURRENT_TIMESTAMP + INTERVAL '10 day',
        'CLOSED',
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
        20101,
        10101,
        9201,
        'PENDING',
        CURRENT_TIMESTAMP - INTERVAL '3 hour',
        NULL,
        CURRENT_TIMESTAMP + INTERVAL '10 hour',
        NULL,
        NULL,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        20102,
        10101,
        9202,
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
        20103,
        10101,
        9203,
        'WAITLISTED',
        CURRENT_TIMESTAMP - INTERVAL '1 hour',
        CURRENT_TIMESTAMP - INTERVAL '1 hour',
        NULL,
        NULL,
        NULL,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    );
