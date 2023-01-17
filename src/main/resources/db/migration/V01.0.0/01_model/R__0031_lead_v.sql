
create or replace view doc_lead_v
as
select	doc.doc_type_id,
				dl.doc_id as id,
				doc.owner_id,
				doc.assignee_id,
				doc.caption,
				--
				doc.case_def_id,
				doc.case_stage_id,
				doc.is_in_work,
				--
				doc.created_by_user_id,
				doc.created_at,
				doc.modified_by_user_id,
				doc.modified_at,
				--
				dl.*,
				--
				doc.version
from		doc_lead dl
join doc on doc.id = dl.doc_id;
