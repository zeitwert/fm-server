
insert into migr_obj_user_v(tenant,name,email,password,role_list,picture) values
('k', 'Lead Processor', 'leads@zeitwert.io', '{noop}zeitwert', 'APP-ADMIN,SAAS-ADMIN,COMMUNITY-ADMIN', null),
('demo', 'Hannes Brunner', 'hannes@zeitwert.io', '{noop}zeitwert', 'APP-ADMIN,SAAS-ADMIN', 'https://randomuser.me/api/portraits/men/7.jpg'),
('demo', 'Martin Frey', 'martin@zeitwert.io', '{noop}zeitwert', 'SAAS-ADMIN', 'https://randomuser.me/api/portraits/men/4.jpg'),
('demo', 'Xavier Frey', 'xavier@zeitwert.io', '{noop}zeitwert', 'SAAS-ADMIN', 'https://randomuser.me/api/portraits/men/5.jpg'),
('demo', 'Simon Schatzmann', 'simon@zeitwert.io', '{noop}zeitwert', 'SAAS-USER', 'https://randomuser.me/api/portraits/men/3.jpg'),
('demo', 'Barbara Buchhalter', 'barbara@zeitwert.io', '{noop}zeitwert', 'COMMUNITY-USER', 'https://randomuser.me/api/portraits/women/3.jpg'),
('demo', 'Verena Verwalterin', 'verena@zeitwert.io', '{noop}zeitwert', 'COMMUNITY-USER,COMMUNITY-ADMIN', 'https://randomuser.me/api/portraits/women/2.jpg');
