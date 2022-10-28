
insert into code_aggregate_type(id, name)
values
('obj_user', 'User')
on conflict(id)
do nothing;

delete from code_user_role
where id in ('appAdmin', 'readOnly');

insert into code_user_role(id, name)
values
('app_admin', 'Application Admin'),
('admin', 'Advisor or Community Tenant Admin'),
('super_user', 'Advisor or Community Tenant User (elevated privileges)'),
('user', 'Advisor or Community Tenant User'),
('read_only', 'Advisor or Community Tenant User Read-Only')
on conflict(id)
do update set name = excluded.name;
