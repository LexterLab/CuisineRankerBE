INSERT INTO profile_pics (name, url, category_id)
VALUES
    ('Rattingam', 'https://storage.googleapis.com/cuisine-media/rat1.jpg', (SELECT id FROM profile_pic_categories WHERE name = 'STARTER')),
    ('Billy', 'https://storage.googleapis.com/cuisine-media/male1.png', (SELECT id FROM profile_pic_categories WHERE name = 'STARTER')),
    ('Jasmine', 'https://storage.googleapis.com/cuisine-media/female4.png', (SELECT id FROM profile_pic_categories WHERE name = 'STARTER')),
    ('William', 'https://storage.googleapis.com/cuisine-media/cat3.png', (SELECT id FROM profile_pic_categories WHERE name = 'STARTER'))
ON CONFLICT DO NOTHING;