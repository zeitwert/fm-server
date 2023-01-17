
create or replace view obj_user_v
as
select	obj.obj_type_id,
				u.obj_id as id,
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
				u.*,
				--
				obj.version
from		obj_user u
join obj on obj.id = u.obj_id;
