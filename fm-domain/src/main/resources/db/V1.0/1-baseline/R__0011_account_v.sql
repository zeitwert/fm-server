
-- execTrigger: 2

drop view if exists obj_account_v;

create or replace view obj_account_v
as
select	obj.obj_type_id,
				a.obj_id as id,
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
				a.*
from		obj_account a
join obj on obj.id = a.obj_id;
