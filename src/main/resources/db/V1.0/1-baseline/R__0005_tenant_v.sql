
drop view if exists obj_tenant_v;

create or replace view obj_tenant_v
as
select	obj.tenant_id,
				obj.obj_type_id,
				t.obj_id as id,
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
				t.*
from		obj_tenant t
join obj on obj.id = t.obj_id;
