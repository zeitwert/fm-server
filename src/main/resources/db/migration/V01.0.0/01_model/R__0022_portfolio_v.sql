
create or replace view obj_portfolio_v
as
select	obj.obj_type_id,
				pf.obj_id as id,
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
				pf.*,
				--
				obj.version
from		obj_portfolio pf
join obj on obj.id = pf.obj_id;
