INSERT INTO profile_pics (name, url, category_id)
VALUES
    ('Rattingam', 'https://storage.googleapis.com/cuisine-media/profile-icons/rat1.jpg', (SELECT id FROM profile_pic_categories WHERE name = 'STARTER')),
    ('Billy', 'https://storage.googleapis.com/cuisine-media/profile-icons/male1.png', (SELECT id FROM profile_pic_categories WHERE name = 'STARTER')),
    ('Jasmine', 'https://storage.googleapis.com/cuisine-media/profile-icons/female4.png', (SELECT id FROM profile_pic_categories WHERE name = 'STARTER')),
    ('William', 'https://storage.googleapis.com/cuisine-media/profile-icons/cat3.png', (SELECT id FROM profile_pic_categories WHERE name = 'STARTER'))
ON CONFLICT DO NOTHING;