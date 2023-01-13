
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

delete from code_contact_role;

insert into code_contact_role(id, name)
values
('councilor', 'Gemeinderat'),
('caretaker', 'Hauswart'),
('other', 'Anderes')
on conflict(id)
do nothing;

insert into code_gender(id, name)
values
('male', 'Male'),
('female', 'Female'),
('other', 'Other')
on conflict(id)
do nothing;

insert into code_salutation(id, name, gender_id)
values
('mr', 'Mr.', 'male'),
('mrs', 'Mrs.', 'female'),
('ms', 'Ms.', 'female')
on conflict(id)
do nothing;

insert into code_title(id, name)
values
('dr', 'Dr.'),
('prof', 'Prof.')
on conflict(id)
do nothing;

insert into code_address_type(id, name)
values
('mail', 'Mail Address'),
('email', 'Email'),
('chat', 'Chat')
on conflict(id)
do nothing;

insert into code_address_channel(id, name, address_type_id)
values
('mail', 'Mail Address', 'mail'),
('email', 'Email Address', 'email'),
('whatsapp', 'Whatsapp', 'chat'),
('signal', 'Signal', 'chat'),
('viber', 'Viber', 'chat'),
('messenger', 'Messenger', 'chat')
on conflict(id)
do nothing;
