
insert into code_part_list_type(id, name)
values
('obj.transitionList', 'Transitions'),
('obj.noteList', 'Notes')
on conflict(id)
do nothing;
