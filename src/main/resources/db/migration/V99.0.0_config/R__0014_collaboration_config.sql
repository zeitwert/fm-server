
insert into code_aggregate_type(id, name)
values
('obj_note', 'Note')
on conflict(id)
do nothing;

insert into code_note_type(id, name)
values
('note', 'Note'),
('call', 'Call'),
('visit', 'Visit')
on conflict(id)
do nothing;
