
create or replace view obj_document_v
as
select	obj.obj_type_id,
				d.obj_id as id,
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
				d.*,
				--
				obj.version
from		obj_document d
join obj on obj.id = d.obj_id;
