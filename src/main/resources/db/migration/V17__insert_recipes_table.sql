INSERT INTO recipes (preparation, user_id, name, type, cook_time_in_minutes, prep_time_in_minutes, picture_url)
VALUES
    ('Just Do it', 2, 'Chicken Breasts', 'Personal', 1, 1, 'https://t3.gstatic.com/licensed-image?q=tbn:ANd9GcT9XfLsoBK13bXlmWzSKj1PSts6ocsDZRsZHpNb4zTGar4WQ5ezGSlI-OdOND9W2BFD')
ON CONFLICT DO NOTHING;