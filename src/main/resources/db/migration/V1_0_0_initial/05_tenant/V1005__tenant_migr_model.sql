
create or replace view migr_obj_tenant_v
as
select  o.id,
        t.extl_key as key,
        t.name
from    obj_tenant t
join obj o on o.id = t.obj_id;

create or replace function insert_migr_obj_tenant_v()
returns trigger
as
$func$
declare
	new_id int;
	k_user_id int;
begin
	select id into k_user_id from obj_user_v where email = 'k@zeitwert.io';
	insert into obj_tenant(obj_id, extl_key, name)
	values (nextval('obj_id_seq'), new.key, new.name)
	returning obj_id
	into new_id;
	insert into obj(id, tenant_id, obj_type_id, caption, owner_id, created_by_user_id)
	values (new_id, new_id, 'obj_tenant', new.name, k_user_id, k_user_id);
	return new;
end
$func$
language plpgsql;

create trigger migr_obj_tenant_v$ins
instead of insert on migr_obj_tenant_v
for each row execute procedure insert_migr_obj_tenant_v();
