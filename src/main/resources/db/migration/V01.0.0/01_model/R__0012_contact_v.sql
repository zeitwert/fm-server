
create or replace view obj_contact_v
as
select	obj.obj_type_id,
				ct.obj_id as id,
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
				ct.*,
				--
				obj.version
from		obj_contact ct
join obj on id = ct.obj_id;
