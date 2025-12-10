
drop view if exists obj_note_v;

create or replace view obj_note_v
as
select	obj.obj_type_id,
				n.obj_id as id,
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
				n.*
from		obj_note n
join obj on obj.id = n.obj_id;
