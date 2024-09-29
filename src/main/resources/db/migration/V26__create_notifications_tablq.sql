CREATE TABLE notifications (
    id SERIAL PRIMARY KEY,
    title VARCHAR(30) NOT NULL,
    message VARCHAR(50) NOT NULL,
    issued TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

ALTER SEQUENCE notifications_id_seq RESTART WITH 1;