
drop view if exists activity_v;
drop view if exists doc_activity_v;

create or replace view doc_activity_v
as
select	d.id,
				d.doc_type_id as aggregate_type_id,
				dpt.seq_nr,
				dpt.timestamp,
				dpt.user_id,
				dpt.tenant_id,
				dpt.changes,
				d.owner_id,
				d.account_id,
				d.doc_type_id,
				d.caption,
				dpt.old_case_stage_id,
				dpt.new_case_stage_id
from		doc_part_transition dpt
left join doc d
on			dpt.doc_id = d.id
order by timestamp desc;
