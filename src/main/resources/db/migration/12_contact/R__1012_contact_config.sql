
insert into code_aggregate_type(id, name)
values
('obj_contact', 'Contact');

insert into code_part_list_type(id, name)
values
('contact.addressList', 'Address'),
('contact.anniversaryList', 'Anniversary');

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
('first', 'First Residence'),
('second', 'Second Residence'),
('vacations', 'Vacations');

insert into code_anniversary_type(id, name)
values
('birthday', 'Birthday'),
('work', 'Work Anniversary'),
('wedding', 'Wedding Anniversary');

insert into code_anniversary_notification(id, name)
values
('all', 'All'),
('calendar', 'Calendar'),
('email', 'Email'),
('whatsapp', 'WhatsApp');

insert into code_anniversary_template(id, name)
values
('template1', 'Template 1'),
('template2', 'Template 2'),
('template3', 'Template 3');

insert into code_contact_role(id, name)
values
('spouse', 'Spouse'),
('child', 'Child'),
('parent', 'Parent'),
('sibling', 'Sibling'),
('extended_family', 'Extended Family'),
('acquaintance', 'Acquaintance'),
('advisor', 'Advisor');

insert into code_interaction_channel(id, name)
values
('chat', 'Chat'),
('meeting', 'Meeting'),
('call', 'Call'),
('visit', 'Visit'),
('whatsapp', 'Whatsapp'),
('email', 'Email');
