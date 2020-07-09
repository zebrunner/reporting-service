DROP TABLE IF EXISTS test_run_artifacts;
CREATE TABLE IF NOT EXISTS test_run_artifacts (
  id SERIAL,
  name VARCHAR(255) NOT NULL,
  link TEXT NOT NULL,
  expires_at TIMESTAMP NULL,
  test_run_id INT NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (test_run_id) REFERENCES test_runs (id)
    ON DELETE CASCADE
    ON UPDATE NO ACTION
);
CREATE INDEX fk_test_run_artifacts_test_runs_idx ON test_run_artifacts (test_run_id);
CREATE UNIQUE INDEX name_test_run_id_unique ON test_run_artifacts (name, test_run_id);