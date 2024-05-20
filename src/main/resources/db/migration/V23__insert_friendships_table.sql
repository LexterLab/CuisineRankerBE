INSERT INTO friendships (status, user_id, friend_id)
VALUES
    ('Blocked', 2, 5),
    ('Pending', 2, 6),
    ('Pending', 1, 6)
ON CONFLICT DO NOTHING;