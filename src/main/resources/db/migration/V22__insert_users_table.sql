INSERT INTO users (first_name, last_name, email, password, is_verified, pic_id)
VALUES
    ('friendly', 'user', 'friendly@gmail.com', '$2a$10$QlGuFk7M/AJgOJqL1vLsM.8rIh9quuInwO8FwiUwWDkpaN530LCXK', true, (SELECT id FROM profile_pics WHERE name = 'William')),
    ('happy', 'user', 'happyuser@gmail.com', '$2a$10$QlGuFk7M/AJgOJqL1vLsM.8rIh9quuInwO8FwiUwWDkpaN530LCXK', true, (SELECT id FROM profile_pics WHERE name = 'William')),
    ('smiling', 'person', 'smilingperson@gmail.com', '$2a$10$QlGuFk7M/AJgOJqL1vLsM.8rIh9quuInwO8FwiUwWDkpaN530LCXK', true, (SELECT id FROM profile_pics WHERE name = 'William')),
    ('cheerful', 'individual', 'cheerfulindiv@gmail.com', '$2a$10$QlGuFk7M/AJgOJqL1vLsM.8rIh9quuInwO8FwiUwWDkpaN530LCXK', true, (SELECT id FROM profile_pics WHERE name = 'William')),
    ('joyful', 'user', 'joyfuluser@gmail.com', '$2a$10$QlGuFk7M/AJgOJqL1vLsM.8rIh9quuInwO8FwiUwWDkpaN530LCXK', true, (SELECT id FROM profile_pics WHERE name = 'William')),
    ('sunny', 'person', 'sunnyperson@gmail.com', '$2a$10$QlGuFk7M/AJgOJqL1vLsM.8rIh9quuInwO8FwiUwWDkpaN530LCXK', true, (SELECT id FROM profile_pics WHERE name = 'William')),
    ('positive', 'individual', 'positiveindiv@gmail.com', '$2a$10$QlGuFk7M/AJgOJqL1vLsM.8rIh9quuInwO8FwiUwWDkpaN530LCXK', true, (SELECT id FROM profile_pics WHERE name = 'William')),
    ('upbeat', 'user', 'upbeatuser@gmail.com', '$2a$10$QlGuFk7M/AJgOJqL1vLsM.8rIh9quuInwO8FwiUwWDkpaN530LCXK', true, (SELECT id FROM profile_pics WHERE name = 'William')),
    ('positivevibes', 'person', 'positivevibes@gmail.com', '$2a$10$QlGuFk7M/AJgOJqL1vLsM.8rIh9quuInwO8FwiUwWDkpaN530LCXK', true, (SELECT id FROM profile_pics WHERE name = 'William')),
    ('optimistic', 'individual', 'optimisticindiv@gmail.com', '$2a$10$QlGuFk7M/AJgOJqL1vLsM.8rIh9quuInwO8FwiUwWDkpaN530LCXK', true, (SELECT id FROM profile_pics WHERE name = 'William')),
    ('joyous', 'user', 'joyoususer@gmail.com', '$2a$10$QlGuFk7M/AJgOJqL1vLsM.8rIh9quuInwO8FwiUwWDkpaN530LCXK', true, (SELECT id FROM profile_pics WHERE name = 'William'))
    ON CONFLICT DO NOTHING;