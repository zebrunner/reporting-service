CREATE TABLE IF NOT EXISTS test_sessions_2 (
    id SERIAL,
    session_id VARCHAR(255) NOT NULL,
    started_at TIMESTAMP NOT NULL,
    ended_at TIMESTAMP,
    desired_capabilities JSONB NULL,
    capabilities JSONB NULL,
    PRIMARY KEY (id)
);
CREATE INDEX test_sessions_session_id_idx_unique ON test_sessions_2 (session_id);

CREATE TABLE IF NOT EXISTS test_test_sessions (
    id SERIAL,
    test_id INT NOT NULL,
    test_session_id INT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (test_id) REFERENCES tests (id)
        ON DELETE CASCADE
        ON UPDATE NO ACTION,
    FOREIGN KEY (test_session_id) REFERENCES test_sessions_2 (id)
        ON DELETE CASCADE
        ON UPDATE NO ACTION
);
