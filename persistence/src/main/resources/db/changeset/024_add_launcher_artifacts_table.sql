DROP TABLE IF EXISTS launcher_artifacts;
CREATE TABLE IF NOT EXISTS launcher_artifacts (
  id SERIAL,
  name VARCHAR(255) NOT NULL,
  link TEXT NOT NULL,
  expires_at TIMESTAMP NULL,
  launcher_id INT NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (launcher_id) REFERENCES launchers (id)
      ON DELETE CASCADE
      ON UPDATE NO ACTION
);
CREATE INDEX fk_launcher_artifacts_launchers_idx ON launcher_artifacts (launcher_id);
CREATE UNIQUE INDEX name_launcher_id_unique ON launcher_artifacts (name, launcher_id);