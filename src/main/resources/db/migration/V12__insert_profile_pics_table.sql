INSERT INTO profile_pics (name, url, category_id)
VALUES
    ('Rattingam', 'https://bit.ly/49PJgT3', (SELECT id FROM profile_pic_categories WHERE name = 'STARTER')),
    ('Billy', 'https://bit.ly/49Mo93R', (SELECT id FROM profile_pic_categories WHERE name = 'STARTER')),
    ('Jasmine', 'https://bit.ly/3WaCGTY', (SELECT id FROM profile_pic_categories WHERE name = 'STARTER')),
    ('William', 'https://bit.ly/4aJQN7f', (SELECT id FROM profile_pic_categories WHERE name = 'STARTER'))
ON CONFLICT DO NOTHING;