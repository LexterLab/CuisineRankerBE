INSERT INTO friendships (status, user_id, friend_id)
    VALUES
    ('Active', 2, 1),
    ('Active', 2, 3)
ON CONFLICT DO NOTHING;