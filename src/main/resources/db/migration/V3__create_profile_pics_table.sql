CREATE TABLE IF NOT EXISTS profile_pics (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    url VARCHAR(255) NOT NULL UNIQUE,
    category_id BIGINT,
    FOREIGN KEY (category_id) REFERENCES profile_pic_categories (id)
);

ALTER SEQUENCE profile_pics_id_seq RESTART WITH 1;