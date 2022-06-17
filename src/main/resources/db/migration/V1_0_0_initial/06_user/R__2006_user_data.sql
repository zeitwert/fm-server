
insert into migr_obj_user_v(tenant,name,email,password,role_list,picture) values
('demo', 'Hannes Brunner', 'hannes@zeitwert.io', '{noop}demo', 'APP-ADMIN,ADMIN,USER', 'https://randomuser.me/api/portraits/lego/4.jpg'),
('demo', 'Martin Frey', 'martin@zeitwert.io', '{noop}demo', 'ADMIN,USER', '/demo/comunas/martin.jpg'),
('demo', 'Xavier Frey', 'xavier@zeitwert.io', '{noop}demo', 'USER', '/demo/comunas/xavier.jpg'),
('demo', 'Simon Schatzmann', 'simon@zeitwert.io', '{noop}demo', 'USER', 'https://randomuser.me/api/portraits/men/3.jpg'),
('demo', 'Barbara Buchhalter', 'barbara@zeitwert.io', '{noop}demo', 'USER', 'https://randomuser.me/api/portraits/women/3.jpg'),
('demo', 'Verena Verwalterin', 'verena@zeitwert.io', '{noop}demo', 'ADMIN,USER', 'https://randomuser.me/api/portraits/women/2.jpg'),
('comunas', 'Martin Frey', 'martin@comunas.ch', '{noop}comunas', 'ADMIN,USER', '/demo/martin.jpg'),
('comunas', 'Xavier Frey', 'xavier@comunas.ch', '{noop}comunas', 'USER', '/demo/xavier.jpg'),
('unteraegeri', 'Admin', 'admin@unteraegeri.ch', '{noop}comunas', 'ADMIN', 'https://randomuser.me/api/portraits/lego/0.jpg'),
('unteraegeri', 'Martin Frey', 'martin.frey@unteraegeri.ch', '{noop}comunas', 'USER', '/demo/comunas/martin.jpg'),
('unteraegeri', 'Gregor Inderwildi', 'gregor.inderwildi@unteraegeri.ch', '{noop}comunas', 'USER', '/demo/unteraegeri/gregor.png'),
('staefa', 'Admin', 'admin@staefa.ch', '{noop}comunas', 'ADMIN', 'https://randomuser.me/api/portraits/lego/0.jpg'),
('staefa', 'Martin Frey', 'martin.frey@staefa.ch', '{noop}comunas', 'USER', '/demo/comunas/martin.jpg'),
('staefa', 'Marlies Morger', 'marlies.morger@staefa.ch', '{noop}comunas', 'USER', null);
