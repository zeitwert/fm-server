
create or replace view migr_obj_user_v
as
select  o.id,
        t.key as tenant,
        u.name,
        u.email,
        u.password,
        u.role_list,
				u.picture
from    obj_user u
join obj o on o.id = u.obj_id
join migr_obj_tenant_v t on t.id = o.tenant_id;

create or replace function insert_migr_obj_user_v()
returns trigger
as
$func$
declare
	user_id int;
	new_id int;
	tenant_id int;
	k_user_id int;
begin
	select max(id) into user_id from obj_user_v where email = new.email;
	if user_id is not null then
		return null;
	end if;
	select id into k_user_id from obj_user_v where email = 'k@zeitwert.io';
	select id into tenant_id from obj_tenant_v where extl_key = new.tenant;
	insert into obj(id, tenant_id, obj_type_id, caption, owner_id, created_by_user_id)
	values (nextval('obj_id_seq'), tenant_id, 'obj_user', new.name, k_user_id, k_user_id)
	returning id
	into new_id;
	insert into obj_user(obj_id, email, name, password, role_list, picture)
	values (new_id, new.email, new.name, new.password, new.role_list, new.picture);
	return new;
end
$func$
language plpgsql;

create trigger migr_obj_user_v$ins
instead of insert on migr_obj_user_v
for each row execute procedure insert_migr_obj_user_v();
