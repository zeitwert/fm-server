
insert into code_aggregate_type(id, name)
values
('doc_task', 'Task')
on conflict(id)
do nothing;

insert into code_case_def(id, doc_type_id, name)
values
('task', 'doc_task', 'Task Standard Process')
on conflict(id)
do
update set name = excluded.name;

insert into code_case_stage(case_def_id, seq_nr, id, case_stage_type_id, name, description, due, action, abstract_case_stage_id)
values
('task', 10, 'task.new',              'initial',      'New',          '', 0, null, null),
('task', 20, 'task.open',             'intermediate', 'Assigned',     '', 0, null, null),
('task', 30, 'task.progress',         'intermediate', 'In Progress',  '', 0, null, null),
('task', 40, 'task.done',             'terminal',     'Done',         '', 0, null, null)
on conflict(id)
do
update set
	name = excluded.name,
	description = excluded.description,
	action = excluded.action,
	abstract_case_stage_id = excluded.abstract_case_stage_id;

delete from code_task_priority where id = 'critical';

insert into code_task_priority(id, name)
values
('low', 'Tief'),
('normal', 'Normal'),
('high', 'Hoch')
on conflict(id)
do
update set name = excluded.name;
