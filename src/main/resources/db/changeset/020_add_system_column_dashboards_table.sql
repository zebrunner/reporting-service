UPDATE dashboards SET hidden = FALSE WHERE title IN ('User Performance', 'Personal');

ALTER TABLE dashboards ADD COLUMN system BOOLEAN NOT NULL DEFAULT FALSE;

UPDATE dashboards SET system = TRUE WHERE title IN ('Failures analysis', 'Stability');