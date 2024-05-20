CREATE TABLE IF NOT EXISTS friendships (
  id SERIAL PRIMARY KEY,
  status VARCHAR(30) NOT NULL,
  user_id BIGINT,
  friend_id BIGINT,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (friend_id) REFERENCES users(id)
);

CREATE OR REPLACE FUNCTION update_timestamp()
    RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_friendships_timestamp
    BEFORE UPDATE ON friendships
    FOR EACH ROW
EXECUTE FUNCTION update_timestamp();

ALTER SEQUENCE friendships_id_seq RESTART WITH 1;