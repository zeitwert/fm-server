
insert into migr_obj_user_v(tenant,name,email,password,role_list,picture) values
('k', 'Zeitwert Admin', 'admin@zeitwert.io', '{noop}admin', 'app_admin', 'https://randomuser.me/api/portraits/lego/6.jpg'),
--
('comunas', 'Admin', 'admin@comunas.ch', '{noop}admin', 'admin', 'https://randomuser.me/api/portraits/lego/0.jpg'),
('comunas', 'Hannes Brunner', 'hannes@comunas.ch', '{noop}comunas', 'super_user', 'https://randomuser.me/api/portraits/lego/4.jpg'),
('comunas', 'Martin Frey', 'martin@comunas.ch', '{noop}comunas', 'super_user', '/demo/comunas/martin.jpg'),
('comunas', 'Xavier Frey', 'xavier@comunas.ch', '{noop}comunas', 'user', '/demo/comunas/xavier.jpg'),
('comunas', 'Morgan Frey', 'morgan@comunas.ch', '{noop}comunas', 'user', '/demo/comunas/morgan.jfif'),
--
('unteraegeri', 'Admin', 'admin@unteraegeri.ch', '{noop}admin', 'admin', 'https://randomuser.me/api/portraits/lego/0.jpg'),
('unteraegeri', 'Martin Frey', 'martin.frey@unteraegeri.ch', '{noop}comunas', 'super_user', '/demo/comunas/martin.jpg'),
('unteraegeri', 'Gregor Inderwildi', 'gregor.inderwildi@unteraegeri.ch', '{noop}comunas', 'user', '/demo/unteraegeri/gregor.png'),
--
('staefa', 'Admin', 'admin@staefa.ch', '{noop}admin', 'admin', 'https://randomuser.me/api/portraits/lego/0.jpg'),
('staefa', 'Martin Frey', 'martin.frey@staefa.ch', '{noop}comunas', 'super_user', '/demo/comunas/martin.jpg'),
('staefa', 'Marlies Morger', 'marlies.morger@staefa.ch', '{noop}comunas', 'read_only', null);

update obj_user
set    role_list = 'app_admin'
where  email = 'k@zeitwert.io';
