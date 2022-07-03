
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
		update	obj
		set			caption = new.name
		where		id = user_id;
		update	obj_user
		set			name = new.name,
						password = new.password,
						role_list = new.role_list,
						picture = new.picture
		where		obj_id = user_id;
		return new;
	end if;
	select id into k_user_id from obj_user_v where email = 'k@zeitwert.io';
	select id into tenant_id from obj_tenant_v where extl_key = new.tenant;
	insert into obj(id, tenant_id, obj_type_id, caption, owner_id, created_by_user_id)
	values (nextval('obj_id_seq'), tenant_id, 'obj_user', new.name, k_user_id, k_user_id)
	returning id
	into new_id;
	insert into obj_user(obj_id, tenant_id, email, name, password, role_list, picture)
	values (new_id, tenant_id, new.email, new.name, new.password, new.role_list, new.picture);
	return new;
end
$func$
language plpgsql;
