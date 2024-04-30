INSERT INTO user_roles (user_id, role_id)
VALUES
    (1, 2),
    (1, 1),
    (2, 1),
    (3, 1)
ON CONFLICT DO NOTHING;
