CREATE TABLE IF NOT EXISTS profile_pic_categories (
     id SERIAL PRIMARY KEY,
     name VARCHAR(255) NOT NULL UNIQUE
);

ALTER SEQUENCE profile_pic_categories_id_seq RESTART WITH 1;