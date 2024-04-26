CREATE TABLE IF NOT EXISTS recipes (
  id SERIAL PRIMARY KEY,
  name VARCHAR(50) NOT NULL,
  preparation TEXT NOT NULL,
  type VARCHAR(50) NOT NULL,
  picture_url VARCHAR(255) DEFAULT 'https://storage.googleapis.com/cuisine-media/defaults/dishes4.png',
  prep_time_in_minutes INTEGER NOT NULL,
  cook_time_in_minutes INTEGER NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  user_id BIGINT,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id)
);

ALTER SEQUENCE recipes_id_seq RESTART WITH 1;