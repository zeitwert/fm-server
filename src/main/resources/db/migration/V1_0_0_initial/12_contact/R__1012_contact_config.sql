
insert into code_aggregate_type(id, name)
values
('obj_contact', 'Contact');

insert into code_part_list_type(id, name)
values
('contact.addressList', 'Address'),
('contact.anniversaryList', 'Anniversary');


insert into code_contact_role(id, name)
values
('spouse', 'Spouse'),
('child', 'Child'),
('parent', 'Parent'),
('sibling', 'Sibling'),
('extended_family', 'Extended Family'),
('acquaintance', 'Acquaintance'),
('advisor', 'Advisor');

insert into code_gender(id, name)
values
('male', 'Male'),
('female', 'Female');

insert into code_salutation(id, name, gender_id)
values
('mr', 'Mr.', 'male'),
('mrs', 'Mrs.', 'female'),
('ms', 'Ms.', 'female');

insert into code_title(id, name)
values
('dr', 'Dr.'),
('prof', 'Prof.');


insert into code_address_type(id, name)
values
('mail', 'Mail Address'),
('email', 'Email'),
('chat', 'Chat');

insert into code_address_channel(id, name, address_type_id)
values
('mail', 'Mail Address', 'mail'),
('email', 'Email Address', 'email'),
('whatsapp', 'Whatsapp', 'chat'),
('signal', 'Signal', 'chat'),
('viber', 'Viber', 'chat');
