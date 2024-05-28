CREATE TABLE social_users (
    id SERIAL PRIMARY KEY,
    provider VARCHAR NOT NULL,
    provider_id VARCHAR NOT NULL UNIQUE,
    user_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

ALTER SEQUENCE social_users_id_seq RESTART WITH 1;