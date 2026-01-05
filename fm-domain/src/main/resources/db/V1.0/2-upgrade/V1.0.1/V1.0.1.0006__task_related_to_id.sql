-- Simplify doc_task model to use single related_to_id column like obj_note
-- Migration from related_obj_id/related_doc_id to related_to_id

-- Drop the view first (it will be recreated by the repeatable migration R__0015_task_v.sql)
DROP VIEW IF EXISTS doc_task_v;

-- Disable all triggers temporarily to avoid deferred constraint issues
SET session_replication_role = replica;

-- Add new column (nullable for now)
ALTER TABLE doc_task ADD COLUMN related_to_id integer;

-- Populate from existing data
UPDATE doc_task SET related_to_id = COALESCE(related_obj_id, related_doc_id);

-- For tasks without any related object, set related_to_id to the tenant_id as fallback
-- This handles orphaned test data - in production, this should not happen
UPDATE doc_task SET related_to_id = tenant_id WHERE related_to_id IS NULL;

-- Re-enable triggers
SET session_replication_role = DEFAULT;

-- Add NOT NULL constraint
ALTER TABLE doc_task ALTER COLUMN related_to_id SET NOT NULL;

-- Drop old indexes
DROP INDEX IF EXISTS doc_task$related_obj;
DROP INDEX IF EXISTS doc_task$related_doc;

-- Drop old columns (this also drops the FK constraints)
ALTER TABLE doc_task DROP COLUMN related_obj_id;
ALTER TABLE doc_task DROP COLUMN related_doc_id;

-- Create new index
CREATE INDEX doc_task$related_to ON doc_task(related_to_id);
