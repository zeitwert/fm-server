
drop view if exists doc_task_v;

create or replace view doc_task_v
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
from 		doc_task dt
join		doc on doc.id = dt.doc_id;
