CREATE TABLE IF NOT EXISTS user_launcher_preferences (
  id SERIAL,
  user_id INT NOT NULL,
  launcher_id INT NOT NULL,
  favorite BOOLEAN NULL DEFAULT FALSE,
  modified_at TIMESTAMP NOT NULL DEFAULT current_timestamp,
  created_at TIMESTAMP NOT NULL DEFAULT current_timestamp,
  PRIMARY KEY (id),
  FOREIGN KEY (user_id) REFERENCES users (id)
      ON DELETE CASCADE
      ON UPDATE NO ACTION,
  FOREIGN KEY (launcher_id) REFERENCES launchers (id)
      ON DELETE CASCADE
      ON UPDATE NO ACTION
);
CREATE UNIQUE INDEX USER_LAUNCHER_PREFERENCES_USER_ID_LAUNCHER_ID_UNIQUE ON user_launcher_preferences (user_id, launcher_id);
CREATE TRIGGER update_timestamp_user_launcher_preferences
    BEFORE INSERT OR UPDATE
    ON user_launcher_preferences
    FOR EACH ROW
EXECUTE PROCEDURE update_timestamp();