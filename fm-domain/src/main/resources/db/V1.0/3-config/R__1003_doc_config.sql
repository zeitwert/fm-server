
insert into code_part_list_type(id, name)
values
('doc.transitionList', 'Transitions'),
('doc.noteList', 'Notes')
on conflict(id)
do nothing;

insert into code_case_stage_type(id, name)
values
('abstract', 'Abstract Stage'),
('initial', 'Initial Stage'),
('intermediate', 'Intermediate Stage'),
('terminal', 'Terminal Stage')
on conflict(id)
do nothing;
