
insert into code_part_list_type(id, name)
values
('doc.areaSet', 'Areas'),
('obj.areaSet', 'Areas')
on conflict(id)
do nothing;
