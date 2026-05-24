INSERT INTO app_user (user_id, name)
VALUES (9401, 'creator-command'),
       (9402, 'applicant-command');

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
    10301,
    9401,
    '통합 테스트 강의',
    '서비스 통합 테스트용 강의',
    15000,
    2,
    CURRENT_TIMESTAMP - INTERVAL '1 day',
    CURRENT_TIMESTAMP + INTERVAL '6 hour',
    CURRENT_TIMESTAMP + INTERVAL '5 day',
    CURRENT_TIMESTAMP + INTERVAL '10 day',
    'OPEN',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);
