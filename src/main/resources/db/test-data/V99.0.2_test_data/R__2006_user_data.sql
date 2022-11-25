
insert into migr_obj_user_v(tenant,name,email,password,role_list,picture) values
('demo', 'Hannes Brunner', 'hannes@zeitwert.io', '{noop}demo', 'super_user', 'https://randomuser.me/api/portraits/lego/4.jpg'),
('demo', 'Martin Frey', 'martin@zeitwert.io', '{noop}demo', 'super_user', '/demo/comunas/martin.jpg'),
('demo', 'Xavier Frey', 'xavier@zeitwert.io', '{noop}demo', 'user', '/demo/comunas/xavier.jpg'),
('demo', 'Morgan Frey', 'morgan@zeitwert.io', '{noop}demo', 'user', '/demo/comunas/morgan.jfif'),
('demo', 'Robert Reader', 'reader@zeitwert.io', '{noop}demo', 'read_only', 'https://randomuser.me/api/portraits/lego/3.jpg'),
('demo', 'Simon Schatzmann', 'simon@zeitwert.io', '{noop}demo', 'user', 'https://randomuser.me/api/portraits/men/3.jpg'),
('demo', 'Barbara Buchhalter', 'barbara@zeitwert.io', '{noop}demo', 'user', 'https://randomuser.me/api/portraits/women/3.jpg'),
('demo', 'Verena Verwalterin', 'verena@zeitwert.io', '{noop}demo', 'super_user', 'https://randomuser.me/api/portraits/women/2.jpg');

-- fix tenant access
insert	into obj_part_item(
					obj_id,
					parent_part_id,
					part_list_type_id,
					seq_nr,
					item_id
				)
select	uv.id,
				0,
				'user.tenantList',
				0,
				uv.tenant_id
from		obj_user_v uv
where		not exists (select null from obj_part_item pi where pi.obj_id = uv.id and pi.part_list_type_id = 'user.tenantList');

-- allow all comunas to access demo
insert	into obj_part_item(
					obj_id,
					parent_part_id,
					part_list_type_id,
					seq_nr,
					item_id
				)
select	id,
				0,
				'user.tenantList',
				(select max(seq_nr)+1 from obj_part_item opi where opi.obj_id = uv.id and opi.part_list_type_id = 'user.tenantList'),
				(select t.obj_id from obj_tenant t where t.extl_key = 'demo')
from		obj_user_v uv
where		email like '%@comunas.ch';

