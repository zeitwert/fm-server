
alter table obj_tenant
add column inflation_rate decimal;

create or replace view obj_tenant_v
as
select	obj.tenant_id,
				obj.obj_type_id,
				t.obj_id as id,
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

alter table obj_account
add column inflation_rate decimal;

create or replace view obj_account_v
as
select	obj.obj_type_id,
				a.obj_id as id,
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
				a.obj_id as account_id,
				--
				a.*
from		obj_account a
join obj on obj.id = a.obj_id;
