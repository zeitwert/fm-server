
insert into migr_obj_user_v(tenant,name,email,password,role_list,picture) values
('k', 'Lead Processor', 'leads@zeitwert.io', '{noop}comunas', 'APP-ADMIN,SAAS-ADMIN,COMMUNITY-ADMIN', null),
('demo', 'Martin Frey', 'martin@zeitwert.io', '{noop}demo', 'SAAS-ADMIN', '/demo/martin.jpg'),
('demo', 'Xavier Frey', 'xavier@zeitwert.io', '{noop}demo', 'SAAS-ADMIN', '/demo/xavier.jpg'),
('demo', 'Hannes Brunner', 'hannes@zeitwert.io', '{noop}demo', 'APP-ADMIN,SAAS-ADMIN', 'https://randomuser.me/api/portraits/men/7.jpg'),
('demo', 'Simon Schatzmann', 'simon@zeitwert.io', '{noop}demo', 'SAAS-USER', 'https://randomuser.me/api/portraits/men/3.jpg'),
('demo', 'Barbara Buchhalter', 'barbara@zeitwert.io', '{noop}demo', 'COMMUNITY-USER', 'https://randomuser.me/api/portraits/women/3.jpg'),
('demo', 'Verena Verwalterin', 'verena@zeitwert.io', '{noop}demo', 'COMMUNITY-USER,COMMUNITY-ADMIN', 'https://randomuser.me/api/portraits/women/2.jpg'),
('comunas', 'Martin Frey', 'martin@comunas.ch', '{noop}comunas', 'SAAS-ADMIN', '/demo/martin.jpg'),
('comunas', 'Xavier Frey', 'xavier@comunas.ch', '{noop}comunas', 'SAAS-ADMIN', '/demo/xavier.jpg'),
('comunas', 'Hannes Brunner', 'hannes@comunas.ch', '{noop}comunas', 'SAAS-ADMIN', 'https://randomuser.me/api/portraits/men/7.jpg');
