
-- Kernel tenant and user

do $$
declare
	tenant_id int;
	user_id int;
begin
	select obj_id into tenant_id from obj_tenant where tenant_type_id = 'kernel' and name = 'Kernel';
	if tenant_id is not null then
		return;
	end if;
	-- ids
	select nextval('obj_id_seq') into tenant_id;
	select nextval('obj_id_seq') into user_id;
	-- tenant
	insert into obj_tenant(obj_id, tenant_type_id, name, tenant_id, key)
	values (tenant_id, 'kernel', 'Kernel', tenant_id, 'kernel');
	insert into obj(id, tenant_id, obj_type_id, caption, owner_id, created_by_user_id)
	values (tenant_id, tenant_id, 'obj_tenant', 'Kernel', user_id, user_id);
	-- user
	insert into obj(id, tenant_id, obj_type_id, caption, owner_id, created_by_user_id)
	values (user_id, tenant_id, 'obj_user', 'k', user_id, user_id);
	insert into obj_user(obj_id, tenant_id, email, name, password, role_list)
	values (user_id, tenant_id, 'k@zeitwert.io', 'k', '{noop}k', 'app_admin');
	-- user.tenantList
	insert into obj_part_item(obj_id,parent_part_id,part_list_type_id,seq_nr,item_id)
	values (user_id,0,'user.tenantList',0,tenant_id);
end $$;
