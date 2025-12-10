
insert into code_aggregate_type(id, name)
values
('obj_tenant', 'Tenant')
on conflict(id)
do nothing;

insert into code_tenant_type(id, name)
values
('kernel', 'Kernel'),
('community', 'Gemeinde'),
('advisor', 'Berater')
on conflict(id)
do
update set name = excluded.name;
