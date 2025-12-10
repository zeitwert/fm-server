
insert into code_aggregate_type(id, name)
values
('obj_contact', 'Contact')
on conflict(id)
do nothing;

insert into code_part_list_type(id, name)
values
('contact.addressList', 'Address')
on conflict(id)
do nothing;

delete from code_contact_role where (id <> 'councilor' and id <> 'caretaker' and id <> 'other');

insert into code_contact_role(id, name)
values
('councilor', 'Gemeinderat'),
('caretaker', 'Hauswart'),
('other', 'Anderes')
on conflict(id)
do
update set name = excluded.name;

insert into code_gender(id, name)
values
('male', 'Mann'),
('female', 'Frau'),
('other', 'Andere')
on conflict(id)
do
update set name = excluded.name;

delete from code_salutation where id = 'ms';

insert into code_salutation(id, name, gender_id)
values
('mr', 'Herr', 'male'),
('mrs', 'Frau', 'female')
on conflict(id)
do
update set name = excluded.name;

insert into code_title(id, name)
values
('dr', 'Dr.'),
('prof', 'Prof.')
on conflict(id)
do
update set name = excluded.name;

insert into code_address_type(id, name)
values
('mail', 'Mail Address'),
('email', 'Email'),
('chat', 'Chat')
on conflict(id)
do
update set name = excluded.name;

insert into code_address_channel(id, name, address_type_id)
values
('mail', 'Mail Address', 'mail'),
('email', 'Email Address', 'email'),
('whatsapp', 'Whatsapp', 'chat'),
('signal', 'Signal', 'chat'),
('viber', 'Viber', 'chat'),
('messenger', 'Messenger', 'chat')
on conflict(id)
do
update set name = excluded.name;
