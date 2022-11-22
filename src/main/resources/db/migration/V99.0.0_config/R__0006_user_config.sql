
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
('admin', 'Tenant Admin'),
('super_user', 'Super User'),
('user', 'User'),
('read_only', 'Read-Only User')
on conflict(id)
do update set name = excluded.name;
