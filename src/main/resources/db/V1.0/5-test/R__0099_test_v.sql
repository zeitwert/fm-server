
drop view if exists obj_test_v;

create or replace view obj_test_v
as
select	obj.obj_type_id,
				ot.obj_id as id,
				obj.version,
				--
				obj.owner_id,
				obj.caption,
				--
				obj.created_by_user_id,
				obj.created_at,
				obj.modified_by_user_id,
				obj.modified_at,
				obj.closed_by_user_id,
				obj.closed_at,
				--
				ot.*
from		obj_test ot
join obj on obj.id = ot.obj_id;

drop view if exists doc_test_v;

create or replace view doc_test_v
as
select	doc.doc_type_id,
				dt.doc_id as id,
				doc.version,
				--
				doc.owner_id,
				doc.caption,
				--
				doc.case_def_id,
				doc.case_stage_id,
				doc.is_in_work,
				doc.assignee_id,
				--
				doc.created_by_user_id,
				doc.created_at,
				doc.modified_by_user_id,
				doc.modified_at,
				--
				dt.*
from		doc_test dt
join doc on doc.id = dt.doc_id;

