DROP INDEX IF EXISTS work_item_unique;

CREATE UNIQUE INDEX work_item_unique ON work_items (test_case_id, jira_id, type, hash_code);