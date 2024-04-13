INSERT INTO profile_pics (name, url, category_id)
VALUES
    ('Rattingam', 'https://i.ibb.co/mbzZL5x/rat1.jpg', (SELECT id FROM profile_pic_categories WHERE name = 'STARTER')),
    ('Billy', 'https://i.ibb.co/HqxfyS4/male1.png', (SELECT id FROM profile_pic_categories WHERE name = 'STARTER')),
    ('Jasmine', 'https://i.ibb.co/MVZcQgh/female4.png', (SELECT id FROM profile_pic_categories WHERE name = 'STARTER')),
    ('William', 'https://i.ibb.co/Hd3bL9r/cat3.png', (SELECT id FROM profile_pic_categories WHERE name = 'STARTER'))
ON CONFLICT DO NOTHING;