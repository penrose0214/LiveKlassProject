DELETE FROM enrollment WHERE lecture_id = 10301;
DELETE FROM lecture WHERE lecture_id = 10301;
DELETE FROM app_user WHERE user_id IN (9401, 9402);
