
-- Kernel tenant and user

do $$
declare
	tenant_id int;
	user_id int;
begin
	-- ids
	select nextval('obj_id_seq') into tenant_id;
	select nextval('obj_id_seq') into user_id;
	-- tenant
	insert into obj_tenant(obj_id, tenant_type_id, extl_key, name)
	values (tenant_id, 'kernel', 'k', 'Kernel');
	insert into obj(id, tenant_id, obj_type_id, caption, owner_id, created_by_user_id)
	values (tenant_id, tenant_id, 'obj_tenant', 'Kernel', user_id, user_id);
	-- user
	insert into obj(id, tenant_id, obj_type_id, caption, owner_id, created_by_user_id)
	values (user_id, tenant_id, 'obj_user', 'k', user_id, user_id);
	insert into obj_user(obj_id, tenant_id, email, name, password, role_list, picture)
	values (user_id, tenant_id, 'k@zeitwert.io', 'k', '{noop}k', 'appAdmin', 'https://randomuser.me/api/portraits/lego/4.jpg');
end $$;
