
drop view if exists activity_v;
drop view if exists obj_activity_v;

create or replace view obj_activity_v
as
select	o.id,
				o.obj_type_id as aggregate_type_id,
				opt.seq_nr,
				opt.timestamp,
				opt.user_id,
				opt.tenant_id,
				opt.changes,
				o.owner_id,
				o.account_id,
				o.obj_type_id,
				o.caption,
				null as old_case_stage_id,
				null as new_case_stage_id
from		obj_part_transition opt
left join obj o
on			opt.obj_id = o.id
order by timestamp desc;
