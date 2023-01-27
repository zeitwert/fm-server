
insert into code_country(id, name)
values
('de', 'Deutschland'),
('es', 'Spanien')
on conflict(id)
do
update set name = excluded.name;
