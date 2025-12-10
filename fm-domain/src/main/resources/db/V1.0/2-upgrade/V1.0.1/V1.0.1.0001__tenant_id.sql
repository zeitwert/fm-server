
alter table obj_tenant
add column
	tenant_id															integer							references obj_tenant(obj_id) deferrable initially deferred;

commit;

update obj_tenant
set tenant_id = obj_id;

commit;

alter table obj_tenant
alter column tenant_id set not null;
