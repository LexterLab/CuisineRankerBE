CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(59) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_verified BOOLEAN DEFAULT FALSE,
    is_two_factor_enabled BOOLEAN DEFAULT FALSE,
    pic_id BIGINT,
    FOREIGN KEY (pic_id) REFERENCES profile_pics(id)
);

ALTER SEQUENCE users_id_seq RESTART WITH 1;