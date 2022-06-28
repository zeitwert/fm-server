
insert into code_aggregate_type(id, name)
values
('obj_tenant', 'Tenant')
on conflict(id)
do nothing;

insert into code_tenant_type(id, name)
values
('kernel', 'Kernel'),
('community', 'Community'),
('advisor', 'Advisor')
on conflict(id)
do nothing;
