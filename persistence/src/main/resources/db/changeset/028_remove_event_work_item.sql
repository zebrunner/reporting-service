DELETE FROM test_work_items WHERE work_item_id IN (SELECT id FROM work_items WHERE TYPE = 'EVENT');

DELETE FROM work_items WHERE type = 'EVENT';
