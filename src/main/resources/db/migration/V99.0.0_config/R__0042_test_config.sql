
insert into code_aggregate_type(id, name)
values
('obj_test', 'Test Object'),
('doc_test', 'Test Order')
on conflict(id)
do nothing;

insert into code_part_list_type(id, name)
values
('test.nodeList', 'Test Node List'),
('test.nodeSet', 'Test Node Set')
on conflict(id)
do nothing;
