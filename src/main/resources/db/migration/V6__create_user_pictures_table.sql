CREATE TABLE IF NOT EXISTS user_pictures (
    user_id BIGINT,
    pic_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (pic_id) REFERENCES profile_pics(id),
    PRIMARY KEY (user_id, pic_id)
);