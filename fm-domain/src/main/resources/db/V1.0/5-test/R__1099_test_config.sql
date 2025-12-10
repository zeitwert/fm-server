
insert into code_aggregate_type(id, name)
values
('obj_test', 'Test Object'),
('doc_test', 'Test Order')
on conflict(id)
do nothing;

insert into code_part_list_type(id, name)
values
('test.nodeList', 'TestNode List'),
('test.testTypeSet', 'Test Type Set')
on conflict(id)
do nothing;

insert into code_test_type(id, name)
values
('type_a', 'Type A'),
('type_b', 'Type B'),
('type_c', 'Type C')
on conflict(id)
do
update set name = excluded.name;

insert into code_case_def(id, doc_type_id, name)
values
('test', 'doc_test', 'Test Process')
on conflict(id)
do
update set name = excluded.name;

insert into code_case_stage(case_def_id, seq_nr, id, case_stage_type_id, name, description, due)
values
('test', 10, 'test.new',              'initial',      'New',          '', 0),
('test', 20, 'test.open',             'intermediate', 'Assigned',     '', 0),
('test', 30, 'test.progress',         'intermediate', 'In Progress',  '', 0),
('test', 40, 'test.done',             'terminal',     'Done',         '', 0)
on conflict(id)
do
update set
	name = excluded.name,
	description = excluded.description;
