
insert into code_aggregate_type(id, name)
values
('obj_user', 'User')
on conflict(id)
do nothing;

insert into code_user_role(id, name)
values
('appAdmin', 'Application Admin (Super User)'),
('admin', 'Advisor or Account Admin'),
('user', 'Advisor or Account User')
on conflict(id)
do nothing;
