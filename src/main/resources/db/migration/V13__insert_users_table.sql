INSERT INTO users (first_name, last_name, email, password, is_verified, pic_id)
VALUES
    ('admin', 'admin', 'admin@gmail.com', '$2a$10$wIB7iNTCMyvseuBo8mW.EOBZQ8UppLG4SXxbLY8EfZT16QHlIDn4y', false, (SELECT id FROM profile_pics WHERE name = 'William')),
    ('user', 'user', 'user@gmail.com', '$2a$10$QlGuFk7M/AJgOJqL1vLsM.8rIh9quuInwO8FwiUwWDkpaN530LCXK', true, (SELECT id FROM profile_pics WHERE name = 'William')),
    ('user2', 'user2', 'usertwo@gmail.com', '$2a$10$Jyd39IPp./QxZiSUjllh8OdVX1JXCSlfnT5AACtoXmj/5Ru1yFXNW', false, (SELECT id FROM profile_pics WHERE name = 'William'))
ON CONFLICT DO NOTHING;
